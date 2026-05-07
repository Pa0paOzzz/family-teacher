package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.service.AdminService;
import com.familyteacher.backend.service.AppointmentService;
import com.familyteacher.backend.service.StudentTutoringRequestService;
import com.familyteacher.backend.service.TeacherJobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TeacherJobPostService teacherJobPostService;

    @Autowired
    private StudentTutoringRequestService studentTutoringRequestService;

    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> userData) {
        return adminService.createUser(userData);
    }

    @PutMapping("/users/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        return adminService.updateUser(id, userData);
    }

    @DeleteMapping("/user/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        return adminService.deleteUser(id);
    }

    @PutMapping("/user/{id}/disable")
    public Map<String, Object> disableUser(@PathVariable Long id) {
        return adminService.disableUser(id);
    }

    @PutMapping("/user/{id}/enable")
    public Map<String, Object> enableUser(@PathVariable Long id) {
        return adminService.enableUser(id);
    }

    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return adminService.getAllTeachers();
    }

    @PutMapping("/teachers/{id}")
    public Map<String, Object> updateTeacherInfo(@PathVariable Long id, @RequestBody Map<String, Object> profileData) {
        return adminService.updateTeacherInfo(id, profileData);
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return adminService.getAllStudents();
    }

    @PutMapping("/students/{id}")
    public Map<String, Object> updateStudentInfo(@PathVariable Long id, @RequestBody Map<String, Object> profileData) {
        return adminService.updateStudentInfo(id, profileData);
    }

    @GetMapping("/appointments")
    public List<Map<String, Object>> getAllAppointments() {
        return adminService.getAllAppointments();
    }

    @GetMapping("/appointments/{id}")
    public Map<String, Object> getAppointmentDetail(@PathVariable Long id) {
        Map<String, Object> detail = adminService.getAppointmentDetail(id);
        if (detail == null) {
            return errorResponse("Appointment not found");
        }
        detail.put("success", true);
        return detail;
    }

    @PutMapping("/appointments/{id}")
    public Map<String, Object> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        String status = updateData.get("status") instanceof String ? (String) updateData.get("status") : null;
        String notes = updateData.get("notes") instanceof String ? (String) updateData.get("notes") : null;

        AppointmentRequest appointment = appointmentService.adminUpdateAppointment(id, status, notes);
        if (appointment == null) {
            return errorResponse("Failed to update appointment");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Appointment updated successfully");
        response.put("status", appointment.getStatus());
        response.put("notes", appointment.getNotes());
        return response;
    }

    @GetMapping("/job-posts")
    public List<Map<String, Object>> getAllJobPosts() {
        return adminService.getAllJobPosts();
    }

    @GetMapping("/job-posts/{id}")
    public Map<String, Object> getJobPostDetail(@PathVariable Long id) {
        Map<String, Object> detail = adminService.getJobPostDetail(id);
        if (detail == null) {
            return errorResponse("Job post not found");
        }
        detail.put("success", true);
        return detail;
    }

    @PutMapping("/job-posts/{id}/status")
    public Map<String, Object> updateJobPostStatus(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        boolean active = !(updateData.get("active") instanceof Boolean boolValue) || boolValue;
        return teacherJobPostService.setJobPostActive(id, active);
    }

    @PutMapping("/job-posts/{id}")
    public Map<String, Object> updateJobPost(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        return teacherJobPostService.adminUpdateJobPost(id, updateData);
    }

    @DeleteMapping("/job-posts/{id}")
    public Map<String, Object> deleteJobPost(@PathVariable Long id) {
        return teacherJobPostService.adminDeleteJobPost(id);
    }

    @GetMapping("/tutoring-requests")
    public List<Map<String, Object>> getAllTutoringRequests() {
        return adminService.getAllTutoringRequests();
    }

    @GetMapping("/tutoring-requests/{id}")
    public Map<String, Object> getTutoringRequestDetail(@PathVariable Long id) {
        Map<String, Object> detail = adminService.getTutoringRequestDetail(id);
        if (detail == null) {
            return errorResponse("Tutoring request not found");
        }
        detail.put("success", true);
        return detail;
    }

    @PutMapping("/tutoring-requests/{id}/status")
    public Map<String, Object> updateTutoringRequestStatus(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        boolean active = !(updateData.get("active") instanceof Boolean boolValue) || boolValue;
        return studentTutoringRequestService.setTutoringRequestActive(id, active);
    }

    @PutMapping("/tutoring-requests/{id}")
    public Map<String, Object> updateTutoringRequest(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        return studentTutoringRequestService.adminUpdateTutoringRequest(id, updateData);
    }

    @DeleteMapping("/tutoring-requests/{id}")
    public Map<String, Object> deleteTutoringRequest(@PathVariable Long id) {
        return studentTutoringRequestService.adminDeleteTutoringRequest(id);
    }

    @GetMapping("/evaluations")
    public List<Map<String, Object>> getAllEvaluations() {
        return adminService.getAllEvaluations();
    }

    @DeleteMapping("/evaluations/{id}")
    public Map<String, Object> deleteEvaluation(@PathVariable Long id) {
        return adminService.deleteEvaluation(id);
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return adminService.getAllOrders();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return adminService.getStatistics();
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
