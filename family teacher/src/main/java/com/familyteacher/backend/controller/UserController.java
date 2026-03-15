package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.service.UserService;
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("role", user.getRole());
            return response;
        }
        // 登录失败，返回错误信息
        Map<String, Object> errorResponse = new java.util.HashMap<>();
        errorResponse.put("error", "Invalid username or password");
        return errorResponse;
    }
    
    @PostMapping("/student/profile")
    public Student createStudentProfile(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }
    
    @PostMapping("/teacher/profile")
    public Teacher createTeacherProfile(@RequestBody Teacher teacher) {
        return teacherService.saveTeacher(teacher);
    }
    
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
    
    @PutMapping("/student/update")
    public Student updateStudentProfile(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }
    
    @PutMapping("/teacher/update")
    public Teacher updateTeacherProfile(@RequestBody Teacher teacher) {
        return teacherService.updateTeacher(teacher);
    }
}