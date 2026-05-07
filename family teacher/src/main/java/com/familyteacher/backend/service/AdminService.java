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
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.TeacherService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    // 获取所有用户
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .sorted(Comparator.comparing(
                        User::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        User::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::buildUserSummary)
                .collect(Collectors.toList());
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

    public Map<String, Object> deleteEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId).orElse(null);
        if (evaluation == null) {
            return errorResponse("评价不存在");
        }

        User evaluated = evaluation.getEvaluated();
        evaluationRepository.delete(evaluation);
        refreshTeacherRating(evaluated);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "评价已删除");
        return response;
    }

    // 删除用户
    public Map<String, Object> deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return errorResponse("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            return errorResponse("管理员账号不能删除");
        }

        user.setEnabled(false);
        user.setDeleted(true);
        userRepository.save(user);
        return successUserResponse("用户已删除", user);
    }

    // 禁用用户
    public Map<String, Object> disableUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return errorResponse("用户不存在");
        }
        if (Boolean.TRUE.equals(user.getDeleted())) {
            return errorResponse("用户已删除");
        }
        if ("ADMIN".equals(user.getRole())) {
            return errorResponse("管理员账号不能禁用");
        }
        user.setEnabled(false);
        userRepository.save(user);
        return successUserResponse("用户已禁用", user);
    }

    public Map<String, Object> enableUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return errorResponse("用户不存在");
        }
        if (Boolean.TRUE.equals(user.getDeleted())) {
            return errorResponse("用户已删除");
        }
        user.setEnabled(true);
        userRepository.save(user);
        return successUserResponse("用户已启用", user);
    }

    @Transactional
    public Map<String, Object> createUser(Map<String, Object> userData) {
        String username = getString(userData, "username");
        String password = getString(userData, "password");
        String role = normalizeRole(getString(userData, "role"));

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(role)) {
            return errorResponse("用户名、密码和角色不能为空");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return errorResponse("用户名已存在");
        }

        String email = getString(userData, "email");
        if (StringUtils.hasText(email) && userRepository.findByEmail(email).isPresent()) {
            return errorResponse("邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setName(getString(userData, "name"));
        user.setEmail(email);
        user.setPhone(getString(userData, "phone"));
        user.setEnabled("ADMIN".equals(role) || getBoolean(userData, "enabled", true));
        userRepository.save(user);

        return successUserResponse("用户已创建", user);
    }

    @Transactional
    public Map<String, Object> updateUser(Long userId, Map<String, Object> userData) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return errorResponse("用户不存在");
        }
        if (Boolean.TRUE.equals(user.getDeleted())) {
            return errorResponse("用户已删除");
        }

        String username = getString(userData, "username");
        if (StringUtils.hasText(username) && !username.equals(user.getUsername())) {
            if (userRepository.findByUsername(username).isPresent()) {
                return errorResponse("用户名已存在");
            }
            user.setUsername(username);
        }

        String email = getString(userData, "email");
        if (StringUtils.hasText(email) && !email.equals(user.getEmail())) {
            if (userRepository.findByEmail(email).isPresent()) {
                return errorResponse("邮箱已存在");
            }
            user.setEmail(email);
        } else if (userData.containsKey("email")) {
            user.setEmail(email);
        }

        if (userData.containsKey("name")) {
            user.setName(getString(userData, "name"));
        }
        if (userData.containsKey("phone")) {
            user.setPhone(getString(userData, "phone"));
        }
        if (userData.containsKey("role")) {
            String role = normalizeRole(getString(userData, "role"));
            if (!StringUtils.hasText(role)) {
                return errorResponse("角色不合法");
            }
            user.setRole(role);
        }
        if ("ADMIN".equals(user.getRole())) {
            user.setEnabled(true);
        }
        if (userData.containsKey("enabled") && !"ADMIN".equals(user.getRole())) {
            user.setEnabled(getBoolean(userData, "enabled", true));
        }
        String password = getString(userData, "password");
        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
        return successUserResponse("用户已更新", user);
    }

    // 获取数据统计
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .count();
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
        item.put("averageScore", calculateAverageScore(evaluation));
        item.put("comment", evaluation.getComment());
        item.put("createdAt", evaluation.getCreatedAt());
        if (appointment != null) {
            item.put("appointmentStatus", appointment.getStatus());
            item.put("appointmentType", appointment.getAppointmentType());
        }
        return item;
    }

    private int calculateAverageScore(Evaluation evaluation) {
        int teachingQuality = evaluation.getTeachingQuality() == null ? 0 : evaluation.getTeachingQuality();
        int attitude = evaluation.getAttitude() == null ? 0 : evaluation.getAttitude();
        int satisfaction = evaluation.getSatisfaction() == null ? 0 : evaluation.getSatisfaction();
        return Math.round((teachingQuality + attitude + satisfaction) / 3.0f);
    }

    private void refreshTeacherRating(User evaluated) {
        if (evaluated == null || !"TEACHER".equals(evaluated.getRole())) {
            return;
        }

        Teacher teacher = teacherRepository.findByUser(evaluated).orElse(null);
        if (teacher == null) {
            return;
        }

        List<Evaluation> evaluations = evaluationRepository.findByEvaluated(evaluated);
        teacher.setReviewCount(evaluations.size());
        if (evaluations.isEmpty()) {
            teacher.setRating(0);
        } else {
            int totalScore = evaluations.stream()
                    .mapToInt(this::calculateAverageScore)
                    .sum();
            teacher.setRating(Math.round(totalScore / (float) evaluations.size()));
        }
        teacherRepository.save(teacher);
    }

    private Map<String, Object> buildUserSummary(User user) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", user.getId());
        item.put("username", user.getUsername());
        item.put("name", user.getName());
        item.put("email", user.getEmail());
        item.put("phone", user.getPhone());
        item.put("role", user.getRole());
        item.put("enabled", !Boolean.FALSE.equals(user.getEnabled()));
        item.put("deleted", Boolean.TRUE.equals(user.getDeleted()));
        item.put("createdAt", user.getCreatedAt());
        item.put("updatedAt", user.getUpdatedAt());
        return item;
    }

    private Map<String, Object> successUserResponse(String message, User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("user", buildUserSummary(user));
        return response;
    }

    private Map<String, Object> errorResponse(String error) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }
        String normalized = role.trim().toUpperCase();
        return switch (normalized) {
            case "ADMIN", "TEACHER", "STUDENT" -> normalized;
            default -> null;
        };
    }

    private Boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            return Boolean.parseBoolean(stringValue);
        }
        return defaultValue;
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value).trim();
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

    @Transactional
    public Map<String, Object> updateStudentInfo(Long studentId, Map<String, Object> profileData) {
        return userService.adminUpdateStudentProfile(studentId, profileData);
    }

    @Transactional
    public Map<String, Object> updateTeacherInfo(Long teacherId, Map<String, Object> profileData) {
        return userService.adminUpdateTeacherProfile(teacherId, profileData);
    }
}
