package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.service.AppointmentService;
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.TeacherService;
import com.familyteacher.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@RestController
@RequestMapping("/api/transactions")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/appointments")
    public Map<String, Object> createAppointment(@RequestBody Map<String, Object> appointmentData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "请先登录");
            return error;
        }

        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "用户不存在");
            return error;
        }

        AppointmentRequest appointment = new AppointmentRequest();

        if ("STUDENT".equals(user.getRole())) {
            Student student = studentService.findByUser(user).orElse(null);
            if (student == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Student profile not found");
                return error;
            }

            Long teacherId = getLongValue(appointmentData.get("teacherId"));
            if (teacherId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Teacher id is required");
                return error;
            }

            Teacher teacher = teacherService.findById(teacherId).orElse(null);
            if (teacher == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Teacher not found");
                return error;
            }

            appointment.setStudent(student);
            appointment.setTeacher(teacher);
        } else if ("TEACHER".equals(user.getRole())) {
            Teacher teacher = teacherService.findByUser(user).orElse(null);
            if (teacher == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Teacher profile not found");
                return error;
            }

            Long studentId = getLongValue(appointmentData.get("studentId"));
            if (studentId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Student id is required");
                return error;
            }

            Student student = studentService.findById(studentId).orElse(null);
            if (student == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Student not found");
                return error;
            }

            appointment.setStudent(student);
            appointment.setTeacher(teacher);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "无权操作");
            return error;
        }

        if (appointmentData.containsKey("appointmentTime")) {
            String appointmentTime = (String) appointmentData.get("appointmentTime");
            if (appointmentTime != null && !appointmentTime.isEmpty()) {
                try {
                    if (appointmentTime.contains("T")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String datePart = appointmentTime.split("T")[0];
                        appointment.setRequestedDate(dateFormat.parse(datePart));

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String timePart = appointmentTime.split("T")[1].split("\\.")[0];
                        appointment.setRequestedTime(timeFormat.format(timeFormat.parse(timePart)));
                    } else if (appointmentTime.contains(" ")) {
                        String[] timeParts = appointmentTime.split(" ");
                        if (timeParts.length >= 2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            appointment.setRequestedDate(dateFormat.parse(timeParts[0]));
                            appointment.setRequestedTime(timeParts[1]);
                        }
                    } else {
                        appointment.setRequestedTime(appointmentTime);
                    }
                } catch (ParseException e) {
                    System.err.println("日期解析失败: " + appointmentTime);
                    e.printStackTrace();
                }
            }
        }

        if (appointmentData.containsKey("subject")) {
            appointment.setSubject((String) appointmentData.get("subject"));
        }

        if (appointmentData.containsKey("location")) {
            appointment.setLocation((String) appointmentData.get("location"));
        }

        if (appointmentData.containsKey("pricePerHour")) {
            Object priceObj = appointmentData.get("pricePerHour");
            if (priceObj instanceof Number) {
                appointment.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }

        if (appointmentData.containsKey("remark")) {
            appointment.setNotes((String) appointmentData.get("remark"));
        }

        appointment.setAppointmentType("TRIAL_INTERVIEW");
        appointment.setStudentConfirmedLongTerm(false);
        appointment.setTeacherConfirmedLongTerm(false);

        AppointmentRequest savedAppointment = appointmentService.createAppointmentRequest(appointment);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Trial appointment created successfully");
        response.put("id", savedAppointment.getId());
        return response;
    }
    
    @GetMapping("/appointments")
    public List<Map<String, Object>> getAppointments(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }
        
        List<AppointmentRequest> appointments;
        if ("STUDENT".equals(user.getRole())) {
            Student student = studentService.findByUser(user).orElse(null);
            if (student != null) {
                appointments = appointmentService.getAppointmentsByStudent(student);
            } else {
                return List.of();
            }
        } else if ("TEACHER".equals(user.getRole())) {
            Teacher teacher = teacherService.findByUser(user).orElse(null);
            if (teacher != null) {
                appointments = appointmentService.getAppointmentsByTeacher(teacher);
            } else {
                return List.of();
            }
        } else {
            return List.of();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (AppointmentRequest appointment : appointments) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", appointment.getId());
            item.put("subject", appointment.getSubject());
            item.put("requestedDate", appointment.getRequestedDate() != null ? dateFormat.format(appointment.getRequestedDate()) : "");
            item.put("requestedTime", appointment.getRequestedTime());
            item.put("location", appointment.getLocation());
            item.put("pricePerHour", appointment.getPricePerHour());
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
                item.put("teacherPhone", appointment.getTeacher().getUser().getPhone() != null ? appointment.getTeacher().getUser().getPhone() : "未填写");
                item.put("teacherEmail", appointment.getTeacher().getUser().getEmail() != null ? appointment.getTeacher().getUser().getEmail() : "未填写");
                item.put("teacherSchool", appointment.getTeacher().getSchool() != null ? appointment.getTeacher().getSchool() : "未填写");
                item.put("teacherMajor", appointment.getTeacher().getMajor() != null ? appointment.getTeacher().getMajor() : "未填写");
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
                item.put("studentPhone", appointment.getStudent().getUser().getPhone() != null ? appointment.getStudent().getUser().getPhone() : "未填写");
                item.put("studentEmail", appointment.getStudent().getUser().getEmail() != null ? appointment.getStudent().getUser().getEmail() : "未填写");
                item.put("studentSchool", appointment.getStudent().getSchool() != null ? appointment.getStudent().getSchool() : "未填写");
                item.put("studentGrade", appointment.getStudent().getGrade() != null ? appointment.getStudent().getGrade() : "未填写");
                item.put("studentMajor", appointment.getStudent().getMajor() != null ? appointment.getStudent().getMajor() : "未填写");
                item.put("studentAddress", appointment.getStudent().getAddressFormatted() != null ? appointment.getStudent().getAddressFormatted() : "未填写");
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
            
            result.add(item);
        }
        
        return result;
    }
    
    @GetMapping("/appointments/{id}")
    public Map<String, Object> getAppointmentById(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return null;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return null;
        }
        
        AppointmentRequest appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return null;
        }
        
        if ("STUDENT".equals(user.getRole())) {
            if (!appointment.getStudent().getUser().getId().equals(user.getId())) {
                return null;
            }
        } else if ("TEACHER".equals(user.getRole())) {
            if (!appointment.getTeacher().getUser().getId().equals(user.getId())) {
                return null;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        result.put("id", appointment.getId());
        result.put("subject", appointment.getSubject());
        result.put("requestedDate", appointment.getRequestedDate() != null ? dateFormat.format(appointment.getRequestedDate()) : "");
        result.put("requestedTime", appointment.getRequestedTime());
        result.put("location", appointment.getLocation());
        result.put("pricePerHour", appointment.getPricePerHour());
        result.put("status", appointment.getStatus());
        result.put("appointmentType", appointment.getAppointmentType());
        result.put("studentConfirmedLongTerm", appointment.getStudentConfirmedLongTerm());
        result.put("teacherConfirmedLongTerm", appointment.getTeacherConfirmedLongTerm());
        result.put("studentConfirmedLongTermCompletion", appointment.getStudentConfirmedLongTermCompletion());
        result.put("teacherConfirmedLongTermCompletion", appointment.getTeacherConfirmedLongTermCompletion());
        result.put("longTermConfirmedAt", appointment.getLongTermConfirmedAt());
        result.put("longTermCompletedAt", appointment.getLongTermCompletedAt());
        result.put("notes", appointment.getNotes());
        result.put("createdAt", appointment.getCreatedAt());
        result.put("updatedAt", appointment.getUpdatedAt());
        
        if (appointment.getTeacher() != null && appointment.getTeacher().getUser() != null) {
            result.put("teacherName", appointment.getTeacher().getUser().getName());
            result.put("teacherId", appointment.getTeacher().getId());
            result.put("teacherUserId", appointment.getTeacher().getUser().getId());
            result.put("teacherPhone", appointment.getTeacher().getUser().getPhone() != null ? appointment.getTeacher().getUser().getPhone() : "未填写");
            result.put("teacherEmail", appointment.getTeacher().getUser().getEmail() != null ? appointment.getTeacher().getUser().getEmail() : "未填写");
            result.put("teacherSchool", appointment.getTeacher().getSchool() != null ? appointment.getTeacher().getSchool() : "未填写");
            result.put("teacherMajor", appointment.getTeacher().getMajor() != null ? appointment.getTeacher().getMajor() : "未填写");
        } else {
            result.put("teacherName", "未知");
            result.put("teacherId", 0);
            result.put("teacherUserId", 0);
            result.put("teacherPhone", "未知");
            result.put("teacherEmail", "未知");
            result.put("teacherSchool", "未知");
            result.put("teacherMajor", "未知");
        }
        
        if (appointment.getStudent() != null && appointment.getStudent().getUser() != null) {
            result.put("studentName", appointment.getStudent().getUser().getName());
            result.put("studentId", appointment.getStudent().getId());
            result.put("studentUserId", appointment.getStudent().getUser().getId());
            result.put("studentPhone", appointment.getStudent().getUser().getPhone() != null ? appointment.getStudent().getUser().getPhone() : "未填写");
            result.put("studentEmail", appointment.getStudent().getUser().getEmail() != null ? appointment.getStudent().getUser().getEmail() : "未填写");
            result.put("studentSchool", appointment.getStudent().getSchool() != null ? appointment.getStudent().getSchool() : "未填写");
            result.put("studentGrade", appointment.getStudent().getGrade() != null ? appointment.getStudent().getGrade() : "未填写");
            result.put("studentMajor", appointment.getStudent().getMajor() != null ? appointment.getStudent().getMajor() : "未填写");
            result.put("studentAddress", appointment.getStudent().getAddressFormatted() != null ? appointment.getStudent().getAddressFormatted() : "未填写");
        } else {
            result.put("studentName", "未知");
            result.put("studentId", 0);
            result.put("studentUserId", 0);
            result.put("studentPhone", "未知");
            result.put("studentEmail", "未知");
            result.put("studentSchool", "未知");
            result.put("studentGrade", "未知");
            result.put("studentMajor", "未知");
            result.put("studentAddress", "未知");
        }
        
        return result;
    }
    
    @PutMapping("/appointments/{id}")
    public Map<String, Object> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> updateData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "请先登录");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "用户不存在");
            return error;
        }
        
        AppointmentRequest appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "预约记录不存在");
            return error;
        }
        
        boolean isStudent = appointment.getStudent() != null
                && appointment.getStudent().getUser() != null
                && appointment.getStudent().getUser().getId().equals(user.getId());
        boolean isTeacher = appointment.getTeacher() != null
                && appointment.getTeacher().getUser() != null
                && appointment.getTeacher().getUser().getId().equals(user.getId());
        if (!isStudent && !isTeacher) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "无权操作该预约");
            return error;
        }
        
        String targetStatus = updateData.containsKey("status") ? (String) updateData.get("status") : appointment.getStatus();

        AppointmentRequest updatedAppointment = appointmentService.updateAppointmentStatus(id, targetStatus, user);
        if (updatedAppointment == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "当前状态不能执行该操作");
            return error;
        }

        if (updateData.containsKey("notes")) {
            updatedAppointment.setNotes((String) updateData.get("notes"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        boolean requestedLongTermCompletion = "LONG_TERM_COMPLETED".equals(targetStatus);
        boolean requestedLongTermRejection = "LONG_TERM_REJECTED".equals(targetStatus);
        boolean longTermCompleted = "LONG_TERM_COMPLETED".equals(updatedAppointment.getStatus());
        response.put("message", longTermCompleted
                ? "长期授课已完成"
                : requestedLongTermCompletion
                    ? "已记录长期授课完成确认，等待对方确认"
                    : requestedLongTermRejection
                        ? "已拒绝长期合作"
                        : "预约状态已更新");
        response.put("status", updatedAppointment.getStatus());
        response.put("studentConfirmedLongTermCompletion", updatedAppointment.getStudentConfirmedLongTermCompletion());
        response.put("teacherConfirmedLongTermCompletion", updatedAppointment.getTeacherConfirmedLongTermCompletion());
        return response;
    }

    @PutMapping("/appointments/{id}/confirm-long-term")
    public Map<String, Object> confirmLongTerm(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "请先登录");
            return error;
        }

        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "用户不存在");
            return error;
        }

        return appointmentService.confirmLongTermCooperation(id, user);
    }
    
    @DeleteMapping("/appointments/{id}")
    public Map<String, Object> deleteAppointment(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "请先登录");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用户不存在");
            return error;
        }
        
        AppointmentRequest appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "预约记录不存在");
            return error;
        }
        
        if ("STUDENT".equals(user.getRole())) {
            if (!appointment.getStudent().getUser().getId().equals(user.getId())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "无权操作该预约");
                return error;
            }
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "无权操作该预约");
            return error;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Appointment deleted successfully");
        return response;
    }
    
    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
