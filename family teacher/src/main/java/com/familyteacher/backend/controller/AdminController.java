package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    // 用户管理
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }
    
    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }
    
    @PutMapping("/user/{id}/disable")
    public User disableUser(@PathVariable Long id) {
        return adminService.disableUser(id);
    }
    
    // 家教老师管理
    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return adminService.getAllTeachers();
    }
    
    // 学生管理
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return adminService.getAllStudents();
    }
    
    // 预约管理
    @GetMapping("/appointments")
    public List<AppointmentRequest> getAllAppointments() {
        return adminService.getAllAppointments();
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