package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.repository.UserRepository;
import com.familyteacher.backend.repository.TeacherRepository;
import com.familyteacher.backend.repository.StudentRepository;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 获取所有家教老师
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    
    // 获取所有学生
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    // 获取所有预约请求
    public List<AppointmentRequest> getAllAppointments() {
        return appointmentRequestRepository.findAll();
    }
    
    // 获取所有订单
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    // 删除用户
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    // 禁用用户
    public User disableUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // 这里可以添加禁用逻辑，例如修改用户状态
            userRepository.save(user);
        }
        return user;
    }
    
    // 获取数据统计
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalStudents = studentRepository.count();
        
        // 预约统计
        long totalAppointments = appointmentRequestRepository.count();
        long pendingAppointments = appointmentRequestRepository.findByStatus("PENDING").size();
        long completedAppointments = appointmentRequestRepository.findByStatus("COMPLETED").size();
        
        // 订单统计
        long totalOrders = orderRepository.count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalStudents", totalStudents);
        stats.put("totalAppointments", totalAppointments);
        stats.put("pendingAppointments", pendingAppointments);
        stats.put("completedAppointments", completedAppointments);
        stats.put("totalOrders", totalOrders);
        
        return stats;
    }
}