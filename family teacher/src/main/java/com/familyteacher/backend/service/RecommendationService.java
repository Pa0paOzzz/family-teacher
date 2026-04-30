package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private static final Set<String> MUNICIPALITIES = Set.of("北京市", "上海市", "天津市", "重庆市");
    private static final Set<String> MUNICIPALITY_PLACEHOLDER_CITIES = Set.of("市辖区", "县");

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;

    public List<TeacherJobPost> recommendTeachersForStudent(Student student) {
        String city = normalizeCity(student == null ? null : student.getAddressCity());
        String province = normalizeProvince(student == null ? null : student.getAddressProvince());
        String district = normalizeDistrict(student == null ? null : student.getAddressDistrict());
        Set<String> preferredSubjects = getStudentPreferredSubjects(student);
        List<TeacherJobPost> allPosts = teacherJobPostRepository.findByActiveTrue().stream()
                .sorted(latestFirstTeacherPost())
                .collect(Collectors.toList());

        return prioritizeByLocationThenSubject(
                allPosts,
                city,
                province,
                district,
                preferredSubjects,
                TeacherJobPost::getSubject,
                TeacherJobPost::getLocationCity,
                TeacherJobPost::getLocationProvince,
                TeacherJobPost::getLocationDistrict
        ).stream().limit(10).collect(Collectors.toList());
    }

    public List<StudentTutoringRequest> recommendStudentsForTeacher(Teacher teacher) {
        String city = normalizeCity(teacher == null ? null : teacher.getAddressCity());
        String province = normalizeProvince(teacher == null ? null : teacher.getAddressProvince());
        String district = normalizeDistrict(teacher == null ? null : teacher.getAddressDistrict());
        Set<String> preferredSubjects = getTeacherPreferredSubjects(teacher);
        List<StudentTutoringRequest> allRequests = studentTutoringRequestRepository.findByActiveTrue().stream()
                .sorted(latestFirstStudentRequest())
                .collect(Collectors.toList());

        return prioritizeByLocationThenSubject(
                allRequests,
                city,
                province,
                district,
                preferredSubjects,
                StudentTutoringRequest::getSubject,
                StudentTutoringRequest::getLocationCity,
                StudentTutoringRequest::getLocationProvince,
                StudentTutoringRequest::getLocationDistrict
        ).stream().limit(10).collect(Collectors.toList());
    }

    private <T> List<T> prioritizeByLocation(List<T> resources,
                                             String profileCity,
                                             String profileProvince,
                                             String profileDistrict,
                                             java.util.function.Function<T, String> cityGetter,
                                             java.util.function.Function<T, String> provinceGetter,
                                             java.util.function.Function<T, String> districtGetter) {
        if (profileCity == null && profileProvince == null && profileDistrict == null) {
            return resources;
        }

        List<T> sameMunicipalityDistrict = new ArrayList<>();
        List<T> sameCity = new ArrayList<>();
        List<T> sameMunicipality = new ArrayList<>();
        List<T> otherLocation = new ArrayList<>();

        for (T resource : resources) {
            String resourceCity = cityGetter.apply(resource);
            String resourceProvince = provinceGetter.apply(resource);
            String resourceDistrict = districtGetter.apply(resource);

            if (isSameMunicipalityDistrict(profileCity, profileProvince, profileDistrict, resourceCity, resourceProvince, resourceDistrict)) {
                sameMunicipalityDistrict.add(resource);
            } else if (isExactSameCity(profileCity, resourceCity)) {
                sameCity.add(resource);
            } else if (isSameMunicipalityArea(profileCity, profileProvince, resourceCity, resourceProvince)) {
                sameMunicipality.add(resource);
            } else {
                otherLocation.add(resource);
            }
        }

        sameMunicipalityDistrict.addAll(sameCity);
        sameMunicipalityDistrict.addAll(sameMunicipality);
        sameMunicipalityDistrict.addAll(otherLocation);
        return sameMunicipalityDistrict;
    }

    private boolean isExactSameCity(String profileCity, String resourceCity) {
        String normalizedProfileCity = normalizeCity(profileCity);
        String normalizedResourceCity = normalizeCity(resourceCity);
        return normalizedProfileCity != null
                && normalizedProfileCity.equals(normalizedResourceCity)
                && !MUNICIPALITY_PLACEHOLDER_CITIES.contains(normalizedProfileCity);
    }

    private boolean isSameMunicipalityDistrict(String profileCity,
                                               String profileProvince,
                                               String profileDistrict,
                                               String resourceCity,
                                               String resourceProvince,
                                               String resourceDistrict) {
        if (!isSameMunicipalityArea(profileCity, profileProvince, resourceCity, resourceProvince)) {
            return false;
        }
        String normalizedProfileDistrict = normalizeDistrict(profileDistrict);
        String normalizedResourceDistrict = normalizeDistrict(resourceDistrict);
        return normalizedProfileDistrict != null && normalizedProfileDistrict.equals(normalizedResourceDistrict);
    }

    private boolean isSameMunicipalityArea(String profileCity, String profileProvince,
                                           String resourceCity, String resourceProvince) {
        String normalizedProfileCity = normalizeCity(profileCity);
        String normalizedProfileProvince = normalizeProvince(profileProvince);
        String normalizedResourceCity = normalizeCity(resourceCity);
        String normalizedResourceProvince = normalizeProvince(resourceProvince);

        String profileMunicipality = getMunicipality(normalizedProfileCity, normalizedProfileProvince);
        if (profileMunicipality == null) {
            return false;
        }

        String resourceMunicipality = getMunicipality(normalizedResourceCity, normalizedResourceProvince);
        return profileMunicipality.equals(resourceMunicipality);
    }

    private String getMunicipality(String city, String province) {
        String cityMunicipality = getMunicipalityFromText(city);
        if (cityMunicipality != null) {
            return cityMunicipality;
        }
        return getMunicipalityFromText(province);
    }

    private String getMunicipalityFromText(String textToCheck) {
        if (!StringUtils.hasText(textToCheck)) {
            return null;
        }

        for (String municipality : MUNICIPALITIES) {
            String normalizedMunicipality = normalizeCity(municipality);
            String shortName = stripMunicipalitySuffix(normalizedMunicipality);
            if (textToCheck.equals(normalizedMunicipality)
                    || textToCheck.equals(shortName)
                    || textToCheck.startsWith(normalizedMunicipality)
                    || textToCheck.startsWith(shortName)) {
                return normalizedMunicipality;
            }
        }
        return null;
    }

    private String stripMunicipalitySuffix(String municipality) {
        return municipality.endsWith("市") ? municipality.substring(0, municipality.length() - 1) : municipality;
    }

    private Comparator<TeacherJobPost> latestFirstTeacherPost() {
        return Comparator.comparing(
                        TeacherJobPost::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        TeacherJobPost::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        TeacherJobPost::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );
    }

    private Comparator<StudentTutoringRequest> latestFirstStudentRequest() {
        return Comparator.comparing(
                        StudentTutoringRequest::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        StudentTutoringRequest::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        StudentTutoringRequest::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );
    }

    private <T> List<T> prioritizeByLocationThenSubject(List<T> resources,
                                                        String profileCity,
                                                        String profileProvince,
                                                        String profileDistrict,
                                                        Set<String> preferredSubjects,
                                                        java.util.function.Function<T, String> subjectGetter,
                                                        java.util.function.Function<T, String> cityGetter,
                                                        java.util.function.Function<T, String> provinceGetter,
                                                        java.util.function.Function<T, String> districtGetter) {
        List<T> prioritizedByLocation = prioritizeByLocation(
                resources,
                profileCity,
                profileProvince,
                profileDistrict,
                cityGetter,
                provinceGetter,
                districtGetter
        );

        if (preferredSubjects == null || preferredSubjects.isEmpty()) {
            return prioritizedByLocation;
        }

        List<T> sameMunicipalityDistrict = new ArrayList<>();
        List<T> sameCity = new ArrayList<>();
        List<T> sameMunicipality = new ArrayList<>();
        List<T> otherLocation = new ArrayList<>();

        for (T resource : prioritizedByLocation) {
            String resourceCity = cityGetter.apply(resource);
            String resourceProvince = provinceGetter.apply(resource);
            String resourceDistrict = districtGetter.apply(resource);

            if (isSameMunicipalityDistrict(profileCity, profileProvince, profileDistrict, resourceCity, resourceProvince, resourceDistrict)) {
                sameMunicipalityDistrict.add(resource);
            } else if (isExactSameCity(profileCity, resourceCity)) {
                sameCity.add(resource);
            } else if (isSameMunicipalityArea(profileCity, profileProvince, resourceCity, resourceProvince)) {
                sameMunicipality.add(resource);
            } else {
                otherLocation.add(resource);
            }
        }

        List<T> prioritized = new ArrayList<>();
        prioritized.addAll(prioritizeBySubject(sameMunicipalityDistrict, preferredSubjects, subjectGetter));
        prioritized.addAll(prioritizeBySubject(sameCity, preferredSubjects, subjectGetter));
        prioritized.addAll(prioritizeBySubject(sameMunicipality, preferredSubjects, subjectGetter));
        prioritized.addAll(prioritizeBySubject(otherLocation, preferredSubjects, subjectGetter));
        return prioritized;
    }

    private <T> List<T> prioritizeBySubject(List<T> resources,
                                            Set<String> preferredSubjects,
                                            java.util.function.Function<T, String> subjectGetter) {
        List<T> sameSubject = new ArrayList<>();
        List<T> otherSubject = new ArrayList<>();

        for (T resource : resources) {
            if (isSubjectMatch(preferredSubjects, subjectGetter.apply(resource))) {
                sameSubject.add(resource);
            } else {
                otherSubject.add(resource);
            }
        }

        sameSubject.addAll(otherSubject);
        return sameSubject;
    }

    private Set<String> getStudentPreferredSubjects(Student student) {
        if (student == null) {
            return Set.of();
        }
        List<StudentTutoringRequest> requests = studentTutoringRequestRepository.findByStudentAndActiveTrue(student);
        if (requests == null) {
            return Set.of();
        }
        return requests.stream()
                .sorted(latestFirstStudentRequest())
                .map(StudentTutoringRequest::getSubject)
                .map(this::normalizeSubject)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> getTeacherPreferredSubjects(Teacher teacher) {
        Set<String> subjects = splitSubjects(teacher == null ? null : teacher.getSubject());
        if (!subjects.isEmpty() || teacher == null) {
            return subjects;
        }

        List<TeacherJobPost> posts = teacherJobPostRepository.findByTeacherAndActiveTrue(teacher);
        if (posts == null) {
            return Set.of();
        }
        return posts.stream()
                .sorted(latestFirstTeacherPost())
                .map(TeacherJobPost::getSubject)
                .map(this::normalizeSubject)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean isSubjectMatch(Collection<String> preferredSubjects, String resourceSubject) {
        String normalizedResourceSubject = normalizeSubject(resourceSubject);
        return normalizedResourceSubject != null && preferredSubjects.contains(normalizedResourceSubject);
    }

    private Set<String> splitSubjects(String subjects) {
        if (!StringUtils.hasText(subjects)) {
            return Set.of();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String subject : subjects.split("[,，、/\\s]+")) {
            String normalizedSubject = normalizeSubject(subject);
            if (normalizedSubject != null) {
                result.add(normalizedSubject);
            }
        }
        return result;
    }

    private String normalizeCity(String city) {
        if (!StringUtils.hasText(city)) {
            return null;
        }
        return city.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeProvince(String province) {
        if (!StringUtils.hasText(province)) {
            return null;
        }
        return province.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeDistrict(String district) {
        if (!StringUtils.hasText(district)) {
            return null;
        }
        return district.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeSubject(String subject) {
        if (!StringUtils.hasText(subject)) {
            return null;
        }
        return subject.trim().toLowerCase(Locale.ROOT);
    }
}
