package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.service.UserService;
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String token = userService.login(username, password);
        if (token != null) {
            User user = userService.findByUsername(username).orElse(null);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            return response;
        }
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid username or password");
        return errorResponse;
    }
    
    @GetMapping("/profile")
    public Map<String, Object> getCurrentUserProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        return userService.getUserProfile(user);
    }
    
    @GetMapping("/student/profile")
    public Map<String, Object> getStudentProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        Map<String, Object> profile = userService.getStudentProfile(user);
        if (profile == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Student profile not found");
            return error;
        }
        return profile;
    }
    
    @GetMapping("/teacher/profile")
    public Map<String, Object> getTeacherProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        Map<String, Object> profile = userService.getTeacherProfile(user);
        if (profile == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Teacher profile not found");
            return error;
        }
        return profile;
    }
    
    @PostMapping("/student/profile")
    public Student createStudentProfile(@RequestBody Student student, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            User user = userService.getUserFromToken(token);
            if (user != null) {
                student.setUser(user);
            }
        }
        return studentService.saveStudent(student);
    }
    
    @PostMapping("/teacher/profile")
    public Teacher createTeacherProfile(@RequestBody Teacher teacher, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            User user = userService.getUserFromToken(token);
            if (user != null) {
                teacher.setUser(user);
            }
        }
        return teacherService.saveTeacher(teacher);
    }
    
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
    
    @PutMapping("/student/update")
    public Map<String, Object> updateStudentProfile(@RequestBody Map<String, Object> profileData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        return userService.updateStudentProfile(user, profileData);
    }
    
    @PutMapping("/teacher/update")
    public Map<String, Object> updateTeacherProfile(@RequestBody Map<String, Object> profileData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        return userService.updateTeacherProfile(user, profileData);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}