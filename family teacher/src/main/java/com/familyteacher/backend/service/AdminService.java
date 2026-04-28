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
import java.text.SimpleDateFormat;
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
    public List<Map<String, Object>> getAllAppointments() {
        return appointmentRequestRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        AppointmentRequest::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        AppointmentRequest::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::buildAppointmentSummary)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAppointmentDetail(Long id) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }
        return buildAppointmentSummary(appointment);
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

    private Map<String, Object> buildAppointmentSummary(AppointmentRequest appointment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", appointment.getId());
        item.put("subject", appointment.getSubject());
        item.put("requestedDate", appointment.getRequestedDate() != null ? dateFormat.format(appointment.getRequestedDate()) : "");
        item.put("requestedTime", appointment.getRequestedTime());
        item.put("location", appointment.getLocation());
        item.put("pricePerHour", appointment.getPricePerHour());
        item.put("durationHours", appointment.getDurationHours());
        item.put("status", appointment.getStatus());
        item.put("appointmentType", appointment.getAppointmentType());
        item.put("studentConfirmedLongTerm", appointment.getStudentConfirmedLongTerm());
        item.put("teacherConfirmedLongTerm", appointment.getTeacherConfirmedLongTerm());
        item.put("studentConfirmedLongTermCompletion", appointment.getStudentConfirmedLongTermCompletion());
        item.put("teacherConfirmedLongTermCompletion", appointment.getTeacherConfirmedLongTermCompletion());
        item.put("longTermConfirmedAt", appointment.getLongTermConfirmedAt());
        item.put("longTermCompletedAt", appointment.getLongTermCompletedAt());
        item.put("notes", appointment.getNotes());
        item.put("createdAt", appointment.getCreatedAt());
        item.put("updatedAt", appointment.getUpdatedAt());

        if (appointment.getTeacher() != null && appointment.getTeacher().getUser() != null) {
            item.put("teacherName", appointment.getTeacher().getUser().getName());
            item.put("teacherId", appointment.getTeacher().getId());
            item.put("teacherUserId", appointment.getTeacher().getUser().getId());
            item.put("teacherPhone", defaultText(appointment.getTeacher().getUser().getPhone()));
            item.put("teacherEmail", defaultText(appointment.getTeacher().getUser().getEmail()));
            item.put("teacherSchool", defaultText(appointment.getTeacher().getSchool()));
            item.put("teacherMajor", defaultText(appointment.getTeacher().getMajor()));
        } else {
            item.put("teacherName", "未知");
            item.put("teacherId", 0);
            item.put("teacherUserId", 0);
            item.put("teacherPhone", "未知");
            item.put("teacherEmail", "未知");
            item.put("teacherSchool", "未知");
            item.put("teacherMajor", "未知");
        }

        if (appointment.getStudent() != null && appointment.getStudent().getUser() != null) {
            item.put("studentName", appointment.getStudent().getUser().getName());
            item.put("studentId", appointment.getStudent().getId());
            item.put("studentUserId", appointment.getStudent().getUser().getId());
            item.put("studentPhone", defaultText(appointment.getStudent().getUser().getPhone()));
            item.put("studentEmail", defaultText(appointment.getStudent().getUser().getEmail()));
            item.put("studentSchool", defaultText(appointment.getStudent().getSchool()));
            item.put("studentGrade", defaultText(appointment.getStudent().getGrade()));
            item.put("studentMajor", defaultText(appointment.getStudent().getMajor()));
            item.put("studentAddress", defaultText(appointment.getStudent().getAddressFormatted()));
        } else {
            item.put("studentName", "未知");
            item.put("studentId", 0);
            item.put("studentUserId", 0);
            item.put("studentPhone", "未知");
            item.put("studentEmail", "未知");
            item.put("studentSchool", "未知");
            item.put("studentGrade", "未知");
            item.put("studentMajor", "未知");
            item.put("studentAddress", "未知");
        }

        return item;
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "未填写" : value;
    }
}
