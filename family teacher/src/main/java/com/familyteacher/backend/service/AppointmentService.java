package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;

    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;
    
    public AppointmentRequest createAppointmentRequest(AppointmentRequest request) {
        if (request.getAppointmentType() == null || request.getAppointmentType().isBlank()) {
            request.setAppointmentType("TRIAL_INTERVIEW");
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
    
    public AppointmentRequest updateAppointmentStatus(Long id, String status) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(status);
            return appointmentRequestRepository.save(appointment);
        }
        return null;
    }
    
    public AppointmentRequest getAppointmentById(Long id) {
        return appointmentRequestRepository.findById(id).orElse(null);
    }

    @Transactional
    public Map<String, Object> confirmLongTermCooperation(Long id, User user) {
        Map<String, Object> response = new HashMap<>();
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null) {
            response.put("success", false);
            response.put("error", "Appointment not found");
            return response;
        }

        if (!"COMPLETED".equals(appointment.getStatus()) && !"LONG_TERM_CONFIRMED".equals(appointment.getStatus())) {
            response.put("success", false);
            response.put("error", "Only completed trial appointments can confirm long-term cooperation");
            return response;
        }

        if (appointment.getStudent() == null || appointment.getTeacher() == null
                || appointment.getStudent().getUser() == null || appointment.getTeacher().getUser() == null) {
            response.put("success", false);
            response.put("error", "Appointment data is incomplete");
            return response;
        }

        Long userId = user == null ? null : user.getId();
        if (Objects.equals(appointment.getStudent().getUser().getId(), userId)) {
            appointment.setStudentConfirmedLongTerm(true);
        } else if (Objects.equals(appointment.getTeacher().getUser().getId(), userId)) {
            appointment.setTeacherConfirmedLongTerm(true);
        } else {
            response.put("success", false);
            response.put("error", "Permission denied");
            return response;
        }

        int closedTeacherJobPosts = 0;
        int closedStudentRequests = 0;
        if (Boolean.TRUE.equals(appointment.getStudentConfirmedLongTerm())
                && Boolean.TRUE.equals(appointment.getTeacherConfirmedLongTerm())) {
            appointment.setStatus("LONG_TERM_CONFIRMED");
            appointment.setLongTermConfirmedAt(new java.util.Date());
            closedTeacherJobPosts = closeMatchedTeacherPosts(appointment.getTeacher(), appointment.getSubject());
            closedStudentRequests = closeMatchedStudentRequests(appointment.getStudent(), appointment.getSubject());
        }

        appointmentRequestRepository.save(appointment);

        response.put("success", true);
        response.put("message", Boolean.TRUE.equals(appointment.getStudentConfirmedLongTerm())
                && Boolean.TRUE.equals(appointment.getTeacherConfirmedLongTerm())
                ? "Long-term cooperation confirmed"
                : "Your long-term cooperation preference has been recorded");
        response.put("studentConfirmedLongTerm", appointment.getStudentConfirmedLongTerm());
        response.put("teacherConfirmedLongTerm", appointment.getTeacherConfirmedLongTerm());
        response.put("status", appointment.getStatus());
        response.put("closedTeacherJobPosts", closedTeacherJobPosts);
        response.put("closedStudentRequests", closedStudentRequests);
        return response;
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
