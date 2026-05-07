package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.OrderRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class AppointmentService {
    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING",
            "ACCEPTED",
            "REJECTED",
            "COMPLETED",
            "LONG_TERM_CONFIRMED",
            "LONG_TERM_REJECTED",
            "LONG_TERM_COMPLETED"
    );

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    public AppointmentRequest createAppointmentRequest(AppointmentRequest request) {
        if (request.getAppointmentType() == null || request.getAppointmentType().isBlank()) {
            request.setAppointmentType("TRIAL_INTERVIEW");
        }
        if (request.getInitiatorRole() == null || request.getInitiatorRole().isBlank()) {
            return null;
        }
        if (request.getStudentConfirmedLongTerm() == null) {
            request.setStudentConfirmedLongTerm(false);
        }
        if (request.getTeacherConfirmedLongTerm() == null) {
            request.setTeacherConfirmedLongTerm(false);
        }
        return appointmentRequestRepository.save(request);
    }

    public List<AppointmentRequest> getAppointmentsByStudent(Student student) {
        return appointmentRequestRepository.findByStudent(student);
    }

    public List<AppointmentRequest> getAppointmentsByTeacher(Teacher teacher) {
        return appointmentRequestRepository.findByTeacher(teacher);
    }

    public List<AppointmentRequest> getAppointmentsByStatus(String status) {
        return appointmentRequestRepository.findByStatus(status);
    }

    public AppointmentRequest updateAppointmentStatus(Long id, String status, User user) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null || user == null) {
            return null;
        }
        if (appointment.getStudent() == null || appointment.getTeacher() == null
                || appointment.getStudent().getUser() == null || appointment.getTeacher().getUser() == null) {
            return null;
        }
        Long userId = user.getId();
        boolean isStudent = Objects.equals(appointment.getStudent().getUser().getId(), userId);
        boolean isTeacher = Objects.equals(appointment.getTeacher().getUser().getId(), userId);
        if (!isStudent && !isTeacher) {
            return null;
        }

        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null || !canTransition(appointment.getStatus(), normalizedStatus, isStudent, isTeacher, appointment.getInitiatorRole())) {
            return null;
        }
        if ("LONG_TERM_REJECTED".equals(normalizedStatus)) {
            appointment.setStatus(normalizedStatus);
            return appointmentRequestRepository.save(appointment);
        }
        if ("LONG_TERM_COMPLETED".equals(normalizedStatus)) {
            return confirmLongTermCompletion(appointment, isStudent, isTeacher);
        }
        appointment.setStatus(normalizedStatus);
        if (!"LONG_TERM_CONFIRMED".equals(normalizedStatus) && !"LONG_TERM_COMPLETED".equals(normalizedStatus)) {
            appointment.setLongTermConfirmedAt(null);
            appointment.setStudentConfirmedLongTermCompletion(false);
            appointment.setTeacherConfirmedLongTermCompletion(false);
            appointment.setLongTermCompletedAt(null);
        }
        return appointmentRequestRepository.save(appointment);
    }

    public AppointmentRequest updateAppointmentStatus(Long id, String status) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }
        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null || !canTransition(appointment.getStatus(), normalizedStatus)) {
            return null;
        }
        appointment.setStatus(normalizedStatus);
        if (!"LONG_TERM_CONFIRMED".equals(normalizedStatus) && !"LONG_TERM_COMPLETED".equals(normalizedStatus)) {
            appointment.setLongTermConfirmedAt(null);
        }
        return appointmentRequestRepository.save(appointment);
    }

    public AppointmentRequest getAppointmentById(Long id) {
        return appointmentRequestRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean cancelPendingAppointment(Long id, User user) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null || user == null) {
            return false;
        }
        if (!"PENDING".equals(appointment.getStatus())
                || appointment.getStudent() == null
                || appointment.getTeacher() == null
                || appointment.getStudent().getUser() == null
                || appointment.getTeacher().getUser() == null) {
            return false;
        }

        Long userId = user.getId();
        boolean isStudent = Objects.equals(appointment.getStudent().getUser().getId(), userId);
        boolean isTeacher = Objects.equals(appointment.getTeacher().getUser().getId(), userId);
        if (!isStudent && !isTeacher) {
            return false;
        }

        String initiatorRole = appointment.getInitiatorRole();
        if ("STUDENT".equalsIgnoreCase(initiatorRole) && !isStudent) {
            return false;
        }
        if ("TEACHER".equalsIgnoreCase(initiatorRole) && !isTeacher) {
            return false;
        }
        if (!StringUtils.hasText(initiatorRole)) {
            return false;
        }

        orderRepository.findByAppointment(appointment).ifPresent(orderRepository::delete);
        appointmentRequestRepository.delete(appointment);
        return true;
    }

    public AppointmentRequest adminUpdateAppointment(Long id, String status, String notes) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }

        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null) {
            return null;
        }

        appointment.setStatus(normalizedStatus);
        if (notes != null) {
            appointment.setNotes(notes);
        }

        switch (normalizedStatus) {
            case "PENDING", "ACCEPTED", "REJECTED", "COMPLETED" -> {
                appointment.setStudentConfirmedLongTerm(false);
                appointment.setTeacherConfirmedLongTerm(false);
                appointment.setLongTermConfirmedAt(null);
                appointment.setStudentConfirmedLongTermCompletion(false);
                appointment.setTeacherConfirmedLongTermCompletion(false);
                appointment.setLongTermCompletedAt(null);
            }
            case "LONG_TERM_CONFIRMED" -> {
                appointment.setStudentConfirmedLongTerm(true);
                appointment.setTeacherConfirmedLongTerm(true);
                if (appointment.getLongTermConfirmedAt() == null) {
                    appointment.setLongTermConfirmedAt(new Date());
                }
                appointment.setStudentConfirmedLongTermCompletion(false);
                appointment.setTeacherConfirmedLongTermCompletion(false);
                appointment.setLongTermCompletedAt(null);
            }
            case "LONG_TERM_REJECTED" -> {
                appointment.setStudentConfirmedLongTermCompletion(false);
                appointment.setTeacherConfirmedLongTermCompletion(false);
                appointment.setLongTermCompletedAt(null);
            }
            case "LONG_TERM_COMPLETED" -> {
                appointment.setStudentConfirmedLongTerm(true);
                appointment.setTeacherConfirmedLongTerm(true);
                appointment.setStudentConfirmedLongTermCompletion(true);
                appointment.setTeacherConfirmedLongTermCompletion(true);
                if (appointment.getLongTermConfirmedAt() == null) {
                    appointment.setLongTermConfirmedAt(new Date());
                }
                if (appointment.getLongTermCompletedAt() == null) {
                    appointment.setLongTermCompletedAt(new Date());
                }
            }
            default -> {
            }
        }

        return appointmentRequestRepository.save(appointment);
    }

    @Transactional
    public Map<String, Object> confirmLongTermCooperation(Long id, User user) {
        Map<String, Object> response = new HashMap<>();
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null) {
            response.put("success", false);
            response.put("error", "预约记录不存在");
            return response;
        }

        if ("LONG_TERM_REJECTED".equals(appointment.getStatus())) {
            response.put("success", false);
            response.put("error", "该预约已拒绝长期合作，不能再次确认");
            return response;
        }
        if (!"COMPLETED".equals(appointment.getStatus()) && !"LONG_TERM_CONFIRMED".equals(appointment.getStatus())) {
            response.put("success", false);
            response.put("error", "只有已完成试课的预约才能确认长期合作");
            return response;
        }

        if (appointment.getStudent() == null || appointment.getTeacher() == null
                || appointment.getStudent().getUser() == null || appointment.getTeacher().getUser() == null) {
            response.put("success", false);
            response.put("error", "预约信息不完整");
            return response;
        }

        Long userId = user == null ? null : user.getId();
        if (Objects.equals(appointment.getStudent().getUser().getId(), userId)) {
            appointment.setStudentConfirmedLongTerm(true);
        } else if (Objects.equals(appointment.getTeacher().getUser().getId(), userId)) {
            appointment.setTeacherConfirmedLongTerm(true);
        } else {
            response.put("success", false);
            response.put("error", "无权操作该预约");
            return response;
        }

        int closedTeacherJobPosts = 0;
        int closedStudentRequests = 0;
        if (Boolean.TRUE.equals(appointment.getStudentConfirmedLongTerm())
                && Boolean.TRUE.equals(appointment.getTeacherConfirmedLongTerm())) {
            appointment.setStatus("LONG_TERM_CONFIRMED");
            appointment.setLongTermConfirmedAt(new Date());
            appointment.setStudentConfirmedLongTermCompletion(false);
            appointment.setTeacherConfirmedLongTermCompletion(false);
            appointment.setLongTermCompletedAt(null);
            closedTeacherJobPosts = closeMatchedTeacherPosts(appointment.getTeacher(), appointment.getSubject());
            closedStudentRequests = closeMatchedStudentRequests(appointment.getStudent(), appointment.getSubject());
        }

        appointmentRequestRepository.save(appointment);

        response.put("success", true);
        response.put("message", Boolean.TRUE.equals(appointment.getStudentConfirmedLongTerm())
                && Boolean.TRUE.equals(appointment.getTeacherConfirmedLongTerm())
                ? "双方已确认长期合作"
                : "已记录长期合作意向，等待对方确认");
        response.put("studentConfirmedLongTerm", appointment.getStudentConfirmedLongTerm());
        response.put("teacherConfirmedLongTerm", appointment.getTeacherConfirmedLongTerm());
        response.put("studentConfirmedLongTermCompletion", appointment.getStudentConfirmedLongTermCompletion());
        response.put("teacherConfirmedLongTermCompletion", appointment.getTeacherConfirmedLongTermCompletion());
        response.put("status", appointment.getStatus());
        response.put("closedTeacherJobPosts", closedTeacherJobPosts);
        response.put("closedStudentRequests", closedStudentRequests);
        return response;
    }

    public boolean canEvaluate(AppointmentRequest appointment) {
        return appointment != null && "LONG_TERM_COMPLETED".equals(appointment.getStatus());
    }

    private AppointmentRequest confirmLongTermCompletion(AppointmentRequest appointment, boolean isStudent, boolean isTeacher) {
        if (!"LONG_TERM_CONFIRMED".equals(appointment.getStatus())) {
            return null;
        }
        if (isStudent) {
            appointment.setStudentConfirmedLongTermCompletion(true);
        }
        if (isTeacher) {
            appointment.setTeacherConfirmedLongTermCompletion(true);
        }
        if (Boolean.TRUE.equals(appointment.getStudentConfirmedLongTermCompletion())
                && Boolean.TRUE.equals(appointment.getTeacherConfirmedLongTermCompletion())) {
            appointment.setStatus("LONG_TERM_COMPLETED");
            appointment.setLongTermCompletedAt(new Date());
        }
        return appointmentRequestRepository.save(appointment);
    }

    private boolean canTransition(String currentStatus, String targetStatus, boolean isStudent, boolean isTeacher) {
        return canTransition(currentStatus, targetStatus, isStudent, isTeacher, null);
    }

    private boolean canTransition(String currentStatus, String targetStatus, boolean isStudent, boolean isTeacher, String initiatorRole) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        if (normalizedCurrent == null || targetStatus == null || !VALID_STATUSES.contains(targetStatus)) {
            return false;
        }
        if (normalizedCurrent.equals(targetStatus)) {
            return true;
        }
        return switch (normalizedCurrent) {
            case "PENDING" -> canPendingTransition(targetStatus, isStudent, isTeacher, initiatorRole);
            case "ACCEPTED" -> isTeacher && "COMPLETED".equals(targetStatus);
            case "COMPLETED" -> (isStudent || isTeacher) && "LONG_TERM_REJECTED".equals(targetStatus);
            case "LONG_TERM_CONFIRMED" -> (isStudent || isTeacher) && "LONG_TERM_COMPLETED".equals(targetStatus);
            default -> false;
        };
    }

    private boolean canTransition(String currentStatus, String targetStatus) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        if (normalizedCurrent == null || targetStatus == null || !VALID_STATUSES.contains(targetStatus)) {
            return false;
        }
        if (normalizedCurrent.equals(targetStatus)) {
            return true;
        }
        return switch (normalizedCurrent) {
            case "PENDING" -> Set.of("ACCEPTED", "REJECTED").contains(targetStatus);
            case "ACCEPTED" -> "COMPLETED".equals(targetStatus);
            case "COMPLETED" -> "LONG_TERM_CONFIRMED".equals(targetStatus);
            case "LONG_TERM_CONFIRMED" -> "LONG_TERM_COMPLETED".equals(targetStatus);
            default -> false;
        };
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        String normalizedStatus = status.trim().toUpperCase();
        return VALID_STATUSES.contains(normalizedStatus) ? normalizedStatus : null;
    }

    private boolean canPendingTransition(String targetStatus, boolean isStudent, boolean isTeacher, String initiatorRole) {
        if (!StringUtils.hasText(initiatorRole)) {
            return false;
        }
        String normalizedInitiator = initiatorRole.trim().toUpperCase();
        if ("STUDENT".equals(normalizedInitiator)) {
            return isTeacher && Set.of("ACCEPTED", "REJECTED").contains(targetStatus);
        }
        if ("TEACHER".equals(normalizedInitiator)) {
            return isStudent && Set.of("ACCEPTED", "REJECTED").contains(targetStatus);
        }
        return false;
    }

    private int closeMatchedTeacherPosts(Teacher teacher, String subject) {
        List<TeacherJobPost> activePosts = teacherJobPostRepository.findByTeacherAndActiveTrue(teacher);
        int count = 0;
        for (TeacherJobPost post : activePosts) {
            if (subjectMatches(subject, post.getSubject())) {
                post.setActive(false);
                teacherJobPostRepository.save(post);
                count++;
            }
        }
        return count;
    }

    private int closeMatchedStudentRequests(Student student, String subject) {
        List<StudentTutoringRequest> activeRequests = studentTutoringRequestRepository.findByStudentAndActiveTrue(student);
        int count = 0;
        for (StudentTutoringRequest request : activeRequests) {
            if (subjectMatches(subject, request.getSubject())) {
                request.setActive(false);
                studentTutoringRequestRepository.save(request);
                count++;
            }
        }
        return count;
    }

    private boolean subjectMatches(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return left.trim().equalsIgnoreCase(right.trim());
    }
}
