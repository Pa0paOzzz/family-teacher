package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.service.AdminService;
import com.familyteacher.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private AppointmentService appointmentService;
    
    // 用户管理
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
    
    // 家教老师管理
    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return adminService.getAllTeachers();
    }

    @PutMapping("/teachers/{id}")
    public Map<String, Object> updateTeacherInfo(@PathVariable Long id, @RequestBody Map<String, Object> profileData) {
        return adminService.updateTeacherInfo(id, profileData);
    }

    // 学生管理
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return adminService.getAllStudents();
    }

    @PutMapping("/students/{id}")
    public Map<String, Object> updateStudentInfo(@PathVariable Long id, @RequestBody Map<String, Object> profileData) {
        return adminService.updateStudentInfo(id, profileData);
    }

    // 预约管理
    @GetMapping("/appointments")
    public List<Map<String, Object>> getAllAppointments() {
        return adminService.getAllAppointments();
    }

    @GetMapping("/appointments/{id}")
    public Map<String, Object> getAppointmentDetail(@PathVariable Long id) {
        Map<String, Object> detail = adminService.getAppointmentDetail(id);
        if (detail == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("success", false);
            error.put("error", "Appointment not found");
            return error;
        }
        detail.put("success", true);
        return detail;
    }

    @PutMapping("/appointments/{id}")
    public Map<String, Object> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        String status = updateData.get("status") instanceof String ? (String) updateData.get("status") : null;
        String notes = updateData.get("notes") instanceof String ? (String) updateData.get("notes") : null;

        AppointmentRequest appointment = appointmentService.adminUpdateAppointment(id, status, notes);
        Map<String, Object> response = new LinkedHashMap<>();
        if (appointment == null) {
            response.put("success", false);
            response.put("error", "Failed to update appointment");
            return response;
        }

        response.put("success", true);
        response.put("message", "Appointment updated successfully");
        response.put("status", appointment.getStatus());
        response.put("notes", appointment.getNotes());
        return response;
    }

    // 评价管理
    @GetMapping("/evaluations")
    public List<Map<String, Object>> getAllEvaluations() {
        return adminService.getAllEvaluations();
    }

    @DeleteMapping("/evaluations/{id}")
    public Map<String, Object> deleteEvaluation(@PathVariable Long id) {
        return adminService.deleteEvaluation(id);
    }

    // 订单管理
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return adminService.getAllOrders();
    }
    
    // 数据统计
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return adminService.getStatistics();
    }
}
