package com.familyteacher.backend.service;

import com.familyteacher.backend.dto.RecommendationResponse;
import com.familyteacher.backend.dto.RecommendationSource;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.Favorite;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.EvaluationRepository;
import com.familyteacher.backend.repository.FavoriteRepository;
import com.familyteacher.backend.repository.StudentRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import com.familyteacher.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private static final Set<String> MUNICIPALITIES = Set.of("北京", "上海", "天津", "重庆");
    private static final Set<String> MUNICIPALITY_PLACEHOLDER_CITIES = Set.of("市辖区", "县");
    private static final int MAX_RECOMMENDATIONS = 10;
    private static final int MAX_NEIGHBORS = 10;

    private static final double FAVORITE_WEIGHT = 3.0;
    private static final double ACCEPTED_WEIGHT = 4.0;
    private static final double COMPLETED_WEIGHT = 6.0;
    private static final double LONG_TERM_CONFIRMED_WEIGHT = 8.0;
    private static final double LONG_TERM_COMPLETED_WEIGHT = 10.0;
    private static final double EVALUATION_WEIGHT = 4.0;

    private static final double CF_WEIGHT = 0.50;
    private static final double LOCATION_WEIGHT = 0.30;
    private static final double SUBJECT_WEIGHT = 0.15;
    private static final double RECENCY_WEIGHT = 0.05;

    private static final String RESOURCE_TYPE_TEACHER_JOB_POST = "TEACHER_JOB_POST";
    private static final String RESOURCE_TYPE_STUDENT_TUTORING_REQUEST = "STUDENT_TUTORING_REQUEST";

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<RecommendationResponse<TeacherJobPost>> recommendTeachersForStudent(Student student) {
        String city = normalizeCity(student == null ? null : student.getAddressCity());
        String province = normalizeProvince(student == null ? null : student.getAddressProvince());
        String district = normalizeDistrict(student == null ? null : student.getAddressDistrict());
        Set<String> preferredSubjects = getStudentPreferredSubjects(student);
        List<TeacherJobPost> allPosts = teacherJobPostRepository.findByActiveTrue().stream()
                .sorted(latestFirstTeacherPost())
                .collect(Collectors.toList());

        List<TeacherJobPost> baseCandidates = prioritizeByLocationThenSubject(
                allPosts,
                city,
                province,
                district,
                preferredSubjects,
                TeacherJobPost::getSubject,
                TeacherJobPost::getLocationCity,
                TeacherJobPost::getLocationProvince,
                TeacherJobPost::getLocationDistrict
        );

        return rankTeacherRecommendations(student, baseCandidates, preferredSubjects, city, province, district);
    }

    public List<RecommendationResponse<StudentTutoringRequest>> recommendStudentsForTeacher(Teacher teacher) {
        String city = normalizeCity(teacher == null ? null : teacher.getAddressCity());
        String province = normalizeProvince(teacher == null ? null : teacher.getAddressProvince());
        String district = normalizeDistrict(teacher == null ? null : teacher.getAddressDistrict());
        Set<String> preferredSubjects = getTeacherPreferredSubjects(teacher);
        List<StudentTutoringRequest> allRequests = studentTutoringRequestRepository.findByActiveTrue().stream()
                .sorted(latestFirstStudentRequest())
                .collect(Collectors.toList());

        List<StudentTutoringRequest> baseCandidates = prioritizeByLocationThenSubject(
                allRequests,
                city,
                province,
                district,
                preferredSubjects,
                StudentTutoringRequest::getSubject,
                StudentTutoringRequest::getLocationCity,
                StudentTutoringRequest::getLocationProvince,
                StudentTutoringRequest::getLocationDistrict
        );

        return rankStudentRecommendations(teacher, baseCandidates, preferredSubjects, city, province, district);
    }

    private List<RecommendationResponse<TeacherJobPost>> rankTeacherRecommendations(Student student,
                                                                                    List<TeacherJobPost> candidates,
                                                                                    Set<String> preferredSubjects,
                                                                                    String city,
                                                                                    String province,
                                                                                    String district) {
        if (student == null || student.getId() == null || candidates.isEmpty()) {
            return buildTeacherFallback(candidates, preferredSubjects, city, province, district);
        }

        Map<Long, TeacherJobPost> candidateById = candidates.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(TeacherJobPost::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        Map<Long, Integer> baseRanks = buildBaseRanks(candidates, TeacherJobPost::getId);
        Map<Long, Double> currentVector = buildStudentInteractionVector(student, candidateById);
        if (currentVector.isEmpty()) {
            return buildTeacherFallback(candidates, preferredSubjects, city, province, district);
        }

        List<SimilarityScore> neighbors = studentRepository.findAll().stream()
                .filter(other -> other != null && other.getId() != null && !other.getId().equals(student.getId()))
                .map(other -> new SimilarityScore(other.getId(), cosineSimilarity(currentVector, buildStudentInteractionVector(other, candidateById))))
                .filter(item -> item.score > 0)
                .sorted(Comparator.comparingDouble((SimilarityScore item) -> item.score).reversed())
                .limit(MAX_NEIGHBORS)
                .collect(Collectors.toList());

        Map<Long, Double> collaborativeScores = aggregateTeacherCollaborativeScores(neighbors, candidateById);
        if (collaborativeScores.values().stream().noneMatch(score -> score != null && score > 0)) {
            return buildTeacherFallback(candidates, preferredSubjects, city, province, district);
        }

        Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);
        List<ScoredRecommendation<TeacherJobPost>> ranked = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            TeacherJobPost candidate = candidates.get(index);
            Long candidateId = candidate.getId();
            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(candidateId, 0.0);
            double locationScore = calculateLocationScore(
                    city,
                    province,
                    district,
                    candidate.getLocationCity(),
                    candidate.getLocationProvince(),
                    candidate.getLocationDistrict()
            );
            double subjectScore = isSubjectMatch(preferredSubjects, candidate.getSubject()) ? 1.0 : 0.0;
            double recencyScore = 1.0 / (baseRanks.getOrDefault(candidateId, index) + 1.0);
            double heuristicScore = LOCATION_WEIGHT * locationScore + SUBJECT_WEIGHT * subjectScore + RECENCY_WEIGHT * recencyScore;
            double finalScore = CF_WEIGHT * collaborativeScore + heuristicScore;
            ranked.add(new ScoredRecommendation<>(
                    candidate,
                    collaborativeScore,
                    heuristicScore,
                    finalScore,
                    buildRecommendationReason(collaborativeScore, locationScore, subjectScore),
                    buildRecommendationSource(collaborativeScore, locationScore, subjectScore),
                    index
            ));
        }

        return ranked.stream()
                .sorted(scoredRecommendationComparator())
                .limit(MAX_RECOMMENDATIONS)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private List<RecommendationResponse<StudentTutoringRequest>> rankStudentRecommendations(Teacher teacher,
                                                                                             List<StudentTutoringRequest> candidates,
                                                                                             Set<String> preferredSubjects,
                                                                                             String city,
                                                                                             String province,
                                                                                             String district) {
        if (teacher == null || teacher.getId() == null || candidates.isEmpty()) {
            return buildStudentFallback(candidates, preferredSubjects, city, province, district);
        }

        Map<Long, StudentTutoringRequest> candidateById = candidates.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(StudentTutoringRequest::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        Map<Long, Integer> baseRanks = buildBaseRanks(candidates, StudentTutoringRequest::getId);
        Map<Long, Double> currentVector = buildTeacherInteractionVector(teacher, candidateById);
        if (currentVector.isEmpty()) {
            return buildStudentFallback(candidates, preferredSubjects, city, province, district);
        }

        List<SimilarityScore> neighbors = teacherRepository.findAll().stream()
                .filter(other -> other != null && other.getId() != null && !other.getId().equals(teacher.getId()))
                .map(other -> new SimilarityScore(other.getId(), cosineSimilarity(currentVector, buildTeacherInteractionVector(other, candidateById))))
                .filter(item -> item.score > 0)
                .sorted(Comparator.comparingDouble((SimilarityScore item) -> item.score).reversed())
                .limit(MAX_NEIGHBORS)
                .collect(Collectors.toList());

        Map<Long, Double> collaborativeScores = aggregateStudentCollaborativeScores(neighbors, candidateById);
        if (collaborativeScores.values().stream().noneMatch(score -> score != null && score > 0)) {
            return buildStudentFallback(candidates, preferredSubjects, city, province, district);
        }

        Map<Long, Double> normalizedCollaborativeScores = normalizeScores(collaborativeScores);
        List<ScoredRecommendation<StudentTutoringRequest>> ranked = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            StudentTutoringRequest candidate = candidates.get(index);
            Long candidateId = candidate.getId();
            double collaborativeScore = normalizedCollaborativeScores.getOrDefault(candidateId, 0.0);
            double locationScore = calculateLocationScore(
                    city,
                    province,
                    district,
                    candidate.getLocationCity(),
                    candidate.getLocationProvince(),
                    candidate.getLocationDistrict()
            );
            double subjectScore = isSubjectMatch(preferredSubjects, candidate.getSubject()) ? 1.0 : 0.0;
            double recencyScore = 1.0 / (baseRanks.getOrDefault(candidateId, index) + 1.0);
            double heuristicScore = LOCATION_WEIGHT * locationScore + SUBJECT_WEIGHT * subjectScore + RECENCY_WEIGHT * recencyScore;
            double finalScore = CF_WEIGHT * collaborativeScore + heuristicScore;
            ranked.add(new ScoredRecommendation<>(
                    candidate,
                    collaborativeScore,
                    heuristicScore,
                    finalScore,
                    buildRecommendationReason(collaborativeScore, locationScore, subjectScore),
                    buildRecommendationSource(collaborativeScore, locationScore, subjectScore),
                    index
            ));
        }

        return ranked.stream()
                .sorted(scoredRecommendationComparator())
                .limit(MAX_RECOMMENDATIONS)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private List<RecommendationResponse<TeacherJobPost>> buildTeacherFallback(List<TeacherJobPost> candidates,
                                                                               Set<String> preferredSubjects,
                                                                               String city,
                                                                               String province,
                                                                               String district) {
        List<RecommendationResponse<TeacherJobPost>> responses = new ArrayList<>();
        for (int index = 0; index < Math.min(candidates.size(), MAX_RECOMMENDATIONS); index++) {
            TeacherJobPost candidate = candidates.get(index);
            double locationScore = calculateLocationScore(
                    city,
                    province,
                    district,
                    candidate.getLocationCity(),
                    candidate.getLocationProvince(),
                    candidate.getLocationDistrict()
            );
            double subjectScore = isSubjectMatch(preferredSubjects, candidate.getSubject()) ? 1.0 : 0.0;
            double recencyScore = 1.0 / (index + 1.0);
            double heuristicScore = LOCATION_WEIGHT * locationScore + SUBJECT_WEIGHT * subjectScore + RECENCY_WEIGHT * recencyScore;
            responses.add(new RecommendationResponse<>(
                    candidate,
                    buildRecommendationReason(0.0, locationScore, subjectScore),
                    buildRecommendationSource(0.0, locationScore, subjectScore).name(),
                    roundScore(heuristicScore),
                    0.0,
                    roundScore(heuristicScore)
            ));
        }
        return responses;
    }

    private List<RecommendationResponse<StudentTutoringRequest>> buildStudentFallback(List<StudentTutoringRequest> candidates,
                                                                                       Set<String> preferredSubjects,
                                                                                       String city,
                                                                                       String province,
                                                                                       String district) {
        List<RecommendationResponse<StudentTutoringRequest>> responses = new ArrayList<>();
        for (int index = 0; index < Math.min(candidates.size(), MAX_RECOMMENDATIONS); index++) {
            StudentTutoringRequest candidate = candidates.get(index);
            double locationScore = calculateLocationScore(
                    city,
                    province,
                    district,
                    candidate.getLocationCity(),
                    candidate.getLocationProvince(),
                    candidate.getLocationDistrict()
            );
            double subjectScore = isSubjectMatch(preferredSubjects, candidate.getSubject()) ? 1.0 : 0.0;
            double recencyScore = 1.0 / (index + 1.0);
            double heuristicScore = LOCATION_WEIGHT * locationScore + SUBJECT_WEIGHT * subjectScore + RECENCY_WEIGHT * recencyScore;
            responses.add(new RecommendationResponse<>(
                    candidate,
                    buildRecommendationReason(0.0, locationScore, subjectScore),
                    buildRecommendationSource(0.0, locationScore, subjectScore).name(),
                    roundScore(heuristicScore),
                    0.0,
                    roundScore(heuristicScore)
            ));
        }
        return responses;
    }

    private Map<Long, Double> buildStudentInteractionVector(Student student, Map<Long, TeacherJobPost> candidateById) {
        if (student == null || student.getId() == null) {
            return Collections.emptyMap();
        }

        Map<Long, Double> vector = new HashMap<>();
        User user = student.getUser();
        if (user != null && user.getId() != null) {
            favoriteRepository.findByUserIdAndResourceType(user.getId(), RESOURCE_TYPE_TEACHER_JOB_POST)
                    .forEach(favorite -> addFavoriteScore(vector, favorite, FAVORITE_WEIGHT, candidateById::containsKey));
            evaluationRepository.findByEvaluator(user)
                    .forEach(evaluation -> addTeacherEvaluationScore(vector, evaluation, candidateById));
        }

        appointmentRequestRepository.findByStudent(student)
                .forEach(appointment -> addTeacherAppointmentScore(vector, appointment, candidateById));

        return vector;
    }

    private Map<Long, Double> buildTeacherInteractionVector(Teacher teacher, Map<Long, StudentTutoringRequest> candidateById) {
        if (teacher == null || teacher.getId() == null) {
            return Collections.emptyMap();
        }

        Map<Long, Double> vector = new HashMap<>();
        User user = teacher.getUser();
        if (user != null && user.getId() != null) {
            favoriteRepository.findByUserIdAndResourceType(user.getId(), RESOURCE_TYPE_STUDENT_TUTORING_REQUEST)
                    .forEach(favorite -> addFavoriteScore(vector, favorite, FAVORITE_WEIGHT, candidateById::containsKey));
            evaluationRepository.findByEvaluator(user)
                    .forEach(evaluation -> addStudentEvaluationScore(vector, evaluation, candidateById));
        }

        appointmentRequestRepository.findByTeacher(teacher)
                .forEach(appointment -> addStudentAppointmentScore(vector, appointment, candidateById));

        return vector;
    }

    private void addFavoriteScore(Map<Long, Double> scores,
                                  Favorite favorite,
                                  double weight,
                                  Function<Long, Boolean> existsPredicate) {
        if (favorite == null || favorite.getResourceId() == null || !existsPredicate.apply(favorite.getResourceId())) {
            return;
        }
        scores.merge(favorite.getResourceId(), weight, Double::sum);
    }

    private void addTeacherAppointmentScore(Map<Long, Double> scores,
                                            AppointmentRequest appointment,
                                            Map<Long, TeacherJobPost> candidateById) {
        double weight = getAppointmentWeight(appointment == null ? null : appointment.getStatus());
        if (weight <= 0 || appointment == null || appointment.getTeacher() == null) {
            return;
        }
        addProjectedTeacherScores(scores, appointment.getTeacher(), appointment.getSubject(), weight, candidateById);
    }

    private void addStudentAppointmentScore(Map<Long, Double> scores,
                                            AppointmentRequest appointment,
                                            Map<Long, StudentTutoringRequest> candidateById) {
        double weight = getAppointmentWeight(appointment == null ? null : appointment.getStatus());
        if (weight <= 0 || appointment == null || appointment.getStudent() == null) {
            return;
        }
        addProjectedStudentScores(scores, appointment.getStudent(), appointment.getSubject(), weight, candidateById);
    }

    private void addTeacherEvaluationScore(Map<Long, Double> scores,
                                           Evaluation evaluation,
                                           Map<Long, TeacherJobPost> candidateById) {
        AppointmentRequest appointment = evaluation == null ? null : evaluation.getAppointment();
        if (appointment == null || appointment.getTeacher() == null) {
            return;
        }
        addProjectedTeacherScores(scores, appointment.getTeacher(), appointment.getSubject(), EVALUATION_WEIGHT, candidateById);
    }

    private void addStudentEvaluationScore(Map<Long, Double> scores,
                                           Evaluation evaluation,
                                           Map<Long, StudentTutoringRequest> candidateById) {
        AppointmentRequest appointment = evaluation == null ? null : evaluation.getAppointment();
        if (appointment == null || appointment.getStudent() == null) {
            return;
        }
        addProjectedStudentScores(scores, appointment.getStudent(), appointment.getSubject(), EVALUATION_WEIGHT, candidateById);
    }

    private void addProjectedTeacherScores(Map<Long, Double> scores,
                                           Teacher teacher,
                                           String subject,
                                           double weight,
                                           Map<Long, TeacherJobPost> candidateById) {
        if (teacher == null || teacher.getId() == null || candidateById.isEmpty()) {
            return;
        }

        List<Long> exactMatchIds = candidateById.values().stream()
                .filter(post -> post.getTeacher() != null && teacher.getId().equals(post.getTeacher().getId()))
                .filter(post -> subjectMatches(subject, post.getSubject()))
                .map(TeacherJobPost::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Long> targetIds = !exactMatchIds.isEmpty() ? exactMatchIds : candidateById.values().stream()
                .filter(post -> post.getTeacher() != null && teacher.getId().equals(post.getTeacher().getId()))
                .map(TeacherJobPost::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        distributeWeight(scores, targetIds, weight);
    }

    private void addProjectedStudentScores(Map<Long, Double> scores,
                                           Student student,
                                           String subject,
                                           double weight,
                                           Map<Long, StudentTutoringRequest> candidateById) {
        if (student == null || student.getId() == null || candidateById.isEmpty()) {
            return;
        }

        List<Long> exactMatchIds = candidateById.values().stream()
                .filter(request -> request.getStudent() != null && student.getId().equals(request.getStudent().getId()))
                .filter(request -> subjectMatches(subject, request.getSubject()))
                .map(StudentTutoringRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Long> targetIds = !exactMatchIds.isEmpty() ? exactMatchIds : candidateById.values().stream()
                .filter(request -> request.getStudent() != null && student.getId().equals(request.getStudent().getId()))
                .map(StudentTutoringRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        distributeWeight(scores, targetIds, weight);
    }

    private void distributeWeight(Map<Long, Double> scores, List<Long> targetIds, double totalWeight) {
        if (targetIds == null || targetIds.isEmpty() || totalWeight <= 0) {
            return;
        }
        double perItemWeight = totalWeight / targetIds.size();
        targetIds.forEach(targetId -> scores.merge(targetId, perItemWeight, Double::sum));
    }

    private Map<Long, Double> aggregateTeacherCollaborativeScores(List<SimilarityScore> neighbors,
                                                                  Map<Long, TeacherJobPost> candidateById) {
        Map<Long, Double> scores = new HashMap<>();
        for (SimilarityScore neighbor : neighbors) {
            Student neighborStudent = studentRepository.findById(neighbor.userId).orElse(null);
            if (neighborStudent == null) {
                continue;
            }
            Map<Long, Double> vector = buildStudentInteractionVector(neighborStudent, candidateById);
            for (Map.Entry<Long, Double> entry : vector.entrySet()) {
                scores.merge(entry.getKey(), neighbor.score * entry.getValue(), Double::sum);
            }
        }
        return scores;
    }

    private Map<Long, Double> aggregateStudentCollaborativeScores(List<SimilarityScore> neighbors,
                                                                  Map<Long, StudentTutoringRequest> candidateById) {
        Map<Long, Double> scores = new HashMap<>();
        for (SimilarityScore neighbor : neighbors) {
            Teacher neighborTeacher = teacherRepository.findById(neighbor.userId).orElse(null);
            if (neighborTeacher == null) {
                continue;
            }
            Map<Long, Double> vector = buildTeacherInteractionVector(neighborTeacher, candidateById);
            for (Map.Entry<Long, Double> entry : vector.entrySet()) {
                scores.merge(entry.getKey(), neighbor.score * entry.getValue(), Double::sum);
            }
        }
        return scores;
    }

    private Map<Long, Double> normalizeScores(Map<Long, Double> scores) {
        if (scores.isEmpty()) {
            return Collections.emptyMap();
        }
        double max = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        if (max <= 0) {
            return scores.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> 0.0));
        }
        return scores.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / max));
    }

    private double cosineSimilarity(Map<Long, Double> leftVector, Map<Long, Double> rightVector) {
        if (leftVector.isEmpty() || rightVector.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        for (Map.Entry<Long, Double> entry : leftVector.entrySet()) {
            dotProduct += entry.getValue() * rightVector.getOrDefault(entry.getKey(), 0.0);
        }

        if (dotProduct <= 0.0) {
            return 0.0;
        }

        double leftNorm = Math.sqrt(leftVector.values().stream().mapToDouble(value -> value * value).sum());
        double rightNorm = Math.sqrt(rightVector.values().stream().mapToDouble(value -> value * value).sum());
        if (leftNorm == 0.0 || rightNorm == 0.0) {
            return 0.0;
        }
        return dotProduct / (leftNorm * rightNorm);
    }

    private double calculateLocationScore(String profileCity,
                                          String profileProvince,
                                          String profileDistrict,
                                          String resourceCity,
                                          String resourceProvince,
                                          String resourceDistrict) {
        if (isSameMunicipalityDistrict(profileCity, profileProvince, profileDistrict, resourceCity, resourceProvince, resourceDistrict)) {
            return 1.0;
        }
        if (isExactSameCity(profileCity, resourceCity)) {
            return 0.7;
        }
        if (isSameMunicipalityArea(profileCity, profileProvince, resourceCity, resourceProvince)) {
            return 0.4;
        }
        return 0.0;
    }

    private double getAppointmentWeight(String status) {
        if (!StringUtils.hasText(status)) {
            return 0.0;
        }
        return switch (status) {
            case "ACCEPTED" -> ACCEPTED_WEIGHT;
            case "COMPLETED" -> COMPLETED_WEIGHT;
            case "LONG_TERM_CONFIRMED" -> LONG_TERM_CONFIRMED_WEIGHT;
            case "LONG_TERM_COMPLETED" -> LONG_TERM_COMPLETED_WEIGHT;
            default -> 0.0;
        };
    }

    private String buildRecommendationReason(double collaborativeScore, double locationScore, double subjectScore) {
        if (collaborativeScore > 0 && (locationScore > 0 || subjectScore > 0)) {
            return "与你相似的用户有过正向互动，且地区/学科匹配";
        }
        if (collaborativeScore > 0) {
            return "与你相似的用户收藏或预约过该信息";
        }
        if (locationScore > 0 && subjectScore > 0) {
            return "地区和学科与您的偏好匹配";
        }
        if (locationScore > 0) {
            return "地区与您的位置接近";
        }
        if (subjectScore > 0) {
            return "学科与您的历史偏好匹配";
        }
        return "近期新发布";
    }

    private RecommendationSource buildRecommendationSource(double collaborativeScore, double locationScore, double subjectScore) {
        if (collaborativeScore > 0 && (locationScore > 0 || subjectScore > 0)) {
            return RecommendationSource.MIXED;
        }
        if (collaborativeScore > 0) {
            return RecommendationSource.COLLABORATIVE;
        }
        if (locationScore > 0 || subjectScore > 0) {
            return RecommendationSource.PROFILE;
        }
        return RecommendationSource.RECENT;
    }

    private <T> Comparator<ScoredRecommendation<T>> scoredRecommendationComparator() {
        return Comparator.<ScoredRecommendation<T>>comparingDouble(item -> item.finalScore)
                .reversed()
                .thenComparingInt(item -> item.baseRank)
                .thenComparing(item -> extractId(item.resource), Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private <T> RecommendationResponse<T> toResponse(ScoredRecommendation<T> recommendation) {
        return new RecommendationResponse<>(
                recommendation.resource,
                recommendation.recommendationReason,
                recommendation.recommendationSource.name(),
                roundScore(recommendation.finalScore),
                roundScore(recommendation.collaborativeScore),
                roundScore(recommendation.heuristicScore)
        );
    }

    private double roundScore(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private Long extractId(Object resource) {
        if (resource instanceof TeacherJobPost post) {
            return post.getId();
        }
        if (resource instanceof StudentTutoringRequest request) {
            return request.getId();
        }
        return null;
    }

    private <T> Map<Long, Integer> buildBaseRanks(List<T> resources, Function<T, Long> idGetter) {
        Map<Long, Integer> baseRanks = new HashMap<>();
        for (int index = 0; index < resources.size(); index++) {
            Long id = idGetter.apply(resources.get(index));
            if (id != null) {
                baseRanks.put(id, index);
            }
        }
        return baseRanks;
    }

    private <T> List<T> prioritizeByLocation(List<T> resources,
                                             String profileCity,
                                             String profileProvince,
                                             String profileDistrict,
                                             Function<T, String> cityGetter,
                                             Function<T, String> provinceGetter,
                                             Function<T, String> districtGetter) {
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

    private boolean isSameMunicipalityArea(String profileCity,
                                           String profileProvince,
                                           String resourceCity,
                                           String resourceProvince) {
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
                                                        Function<T, String> subjectGetter,
                                                        Function<T, String> cityGetter,
                                                        Function<T, String> provinceGetter,
                                                        Function<T, String> districtGetter) {
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
                                            Function<T, String> subjectGetter) {
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

    private boolean subjectMatches(String subjectA, String subjectB) {
        return Objects.equals(normalizeSubject(subjectA), normalizeSubject(subjectB));
    }

    private Set<String> splitSubjects(String subjects) {
        if (!StringUtils.hasText(subjects)) {
            return Set.of();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String subject : subjects.split("[,，、\\s]+")) {
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

    private static final class SimilarityScore {
        private final Long userId;
        private final double score;

        private SimilarityScore(Long userId, double score) {
            this.userId = userId;
            this.score = score;
        }
    }

    private static final class ScoredRecommendation<T> {
        private final T resource;
        private final double collaborativeScore;
        private final double heuristicScore;
        private final double finalScore;
        private final String recommendationReason;
        private final RecommendationSource recommendationSource;
        private final int baseRank;

        private ScoredRecommendation(T resource,
                                     double collaborativeScore,
                                     double heuristicScore,
                                     double finalScore,
                                     String recommendationReason,
                                     RecommendationSource recommendationSource,
                                     int baseRank) {
            this.resource = resource;
            this.collaborativeScore = collaborativeScore;
            this.heuristicScore = heuristicScore;
            this.finalScore = finalScore;
            this.recommendationReason = recommendationReason;
            this.recommendationSource = recommendationSource;
            this.baseRank = baseRank;
        }
    }
}
