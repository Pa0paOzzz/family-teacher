package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.repository.UserRepository;
import com.familyteacher.backend.repository.TeacherRepository;
import com.familyteacher.backend.repository.StudentRepository;
import com.familyteacher.backend.repository.AppointmentRequestRepository;
import com.familyteacher.backend.repository.OrderRepository;
import com.familyteacher.backend.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

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

    @Autowired
    private EvaluationRepository evaluationRepository;

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

    // 获取所有评价
    public List<Map<String, Object>> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Evaluation::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        Evaluation::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::buildEvaluationSummary)
                .collect(Collectors.toList());
    }

    // 删除用户
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 禁用用户
    public User disableUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            userRepository.save(user);
        }
        return user;
    }

    // 获取数据统计
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalStudents = studentRepository.count();
        long totalAppointments = appointmentRequestRepository.count();
        long pendingAppointments = appointmentRequestRepository.findByStatus("PENDING").size();
        long completedAppointments = appointmentRequestRepository.findByStatus("COMPLETED").size();
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

    private Map<String, Object> buildEvaluationSummary(Evaluation evaluation) {
        AppointmentRequest appointment = evaluation.getAppointment();
        User evaluator = evaluation.getEvaluator();
        User evaluated = evaluation.getEvaluated();

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", evaluation.getId());
        item.put("appointmentId", appointment != null ? appointment.getId() : null);
        item.put("subject", appointment != null ? appointment.getSubject() : null);
        item.put("requestedDate", appointment != null ? appointment.getRequestedDate() : null);
        item.put("requestedTime", appointment != null ? appointment.getRequestedTime() : null);
        item.put("location", appointment != null ? appointment.getLocation() : null);
        item.put("evaluatorId", evaluator != null ? evaluator.getId() : null);
        item.put("evaluatorName", evaluator != null ? evaluator.getName() : null);
        item.put("evaluatorRole", evaluator != null ? evaluator.getRole() : null);
        item.put("evaluatedId", evaluated != null ? evaluated.getId() : null);
        item.put("evaluatedName", evaluated != null ? evaluated.getName() : null);
        item.put("evaluatedRole", evaluated != null ? evaluated.getRole() : null);
        item.put("teachingQuality", evaluation.getTeachingQuality());
        item.put("attitude", evaluation.getAttitude());
        item.put("satisfaction", evaluation.getSatisfaction());
        item.put("comment", evaluation.getComment());
        item.put("createdAt", evaluation.getCreatedAt());
        return item;
    }
}

