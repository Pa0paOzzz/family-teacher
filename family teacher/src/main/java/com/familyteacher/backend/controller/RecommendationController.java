package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.service.RecommendationService;
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.TeacherService;
import com.familyteacher.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/teachers-for-student")
    public List<TeacherJobPost> recommendTeachersForStudent(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return List.of();
        }

        Optional<Student> studentOptional = studentService.findByUser(user);
        if (studentOptional.isEmpty()) {
            return List.of();
        }

        return recommendationService.recommendTeachersForStudent(studentOptional.get());
    }

    @GetMapping("/students-for-teacher")
    public List<StudentTutoringRequest> recommendStudentsForTeacher(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return List.of();
        }

        Optional<Teacher> teacherOptional = teacherService.findByUser(user);
        if (teacherOptional.isEmpty()) {
            return List.of();
        }

        return recommendationService.recommendStudentsForTeacher(teacherOptional.get());
    }

    private User getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return null;
        }
        return userService.getUserFromToken(token);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
