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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private static final Set<String> MUNICIPALITIES = Set.of("北京市", "上海市", "天津市", "重庆市");

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;

    public List<TeacherJobPost> recommendTeachersForStudent(Student student) {
        String city = normalizeCity(student == null ? null : student.getAddressCity());
        String province = normalizeProvince(student == null ? null : student.getAddressProvince());
        String district = normalizeDistrict(student == null ? null : student.getAddressDistrict());
        List<TeacherJobPost> allPosts = teacherJobPostRepository.findByActiveTrue().stream()
                .sorted(latestFirstTeacherPost())
                .collect(Collectors.toList());

        return prioritizeByLocation(
                allPosts,
                city,
                province,
                district,
                TeacherJobPost::getLocationCity,
                TeacherJobPost::getLocationProvince,
                TeacherJobPost::getLocationDistrict
        ).stream().limit(10).collect(Collectors.toList());
    }

    public List<StudentTutoringRequest> recommendStudentsForTeacher(Teacher teacher) {
        String city = normalizeCity(teacher == null ? null : teacher.getAddressCity());
        String province = normalizeProvince(teacher == null ? null : teacher.getAddressProvince());
        String district = normalizeDistrict(teacher == null ? null : teacher.getAddressDistrict());
        List<StudentTutoringRequest> allRequests = studentTutoringRequestRepository.findByActiveTrue().stream()
                .sorted(latestFirstStudentRequest())
                .collect(Collectors.toList());

        return prioritizeByLocation(
                allRequests,
                city,
                province,
                district,
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

        List<T> sameCity = new ArrayList<>();
        List<T> sameMunicipalityDistrict = new ArrayList<>();
        List<T> sameMunicipality = new ArrayList<>();
        List<T> otherLocation = new ArrayList<>();

        for (T resource : resources) {
            String resourceCity = cityGetter.apply(resource);
            String resourceProvince = provinceGetter.apply(resource);
            String resourceDistrict = districtGetter.apply(resource);

            if (isExactSameCity(profileCity, resourceCity)) {
                sameCity.add(resource);
            } else if (isSameMunicipalityDistrict(profileCity, profileProvince, profileDistrict, resourceCity, resourceProvince, resourceDistrict)) {
                sameMunicipalityDistrict.add(resource);
            } else if (isSameMunicipalityArea(profileCity, profileProvince, resourceCity, resourceProvince)) {
                sameMunicipality.add(resource);
            } else {
                otherLocation.add(resource);
            }
        }

        sameCity.addAll(sameMunicipalityDistrict);
        sameCity.addAll(sameMunicipality);
        sameCity.addAll(otherLocation);
        return sameCity;
    }

    private boolean isExactSameCity(String profileCity, String resourceCity) {
        String normalizedProfileCity = normalizeCity(profileCity);
        String normalizedResourceCity = normalizeCity(resourceCity);
        return normalizedProfileCity != null && normalizedProfileCity.equals(normalizedResourceCity);
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
        if (!StringUtils.hasText(city) && !StringUtils.hasText(province)) {
            return null;
        }

        String textToCheck = StringUtils.hasText(city) ? city : province;

        for (String municipality : MUNICIPALITIES) {
            String normalizedMunicipality = normalizeCity(municipality);
            String shortName = normalizedMunicipality.replace("市", "");
            if (textToCheck.equals(normalizedMunicipality)
                    || textToCheck.equals(shortName)
                    || textToCheck.startsWith(normalizedMunicipality)
                    || textToCheck.startsWith(shortName)) {
                return normalizedMunicipality;
            }
        }
        return null;
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
}
