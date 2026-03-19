package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.repository.UserRepository;
import com.familyteacher.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public String login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(username);
            }
        }
        return null;
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public User getUserFromToken(String token) {
        if (token == null) {
            return null;
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public Map<String, Object> getUserProfile(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("role", user.getRole());
        
        if ("STUDENT".equals(user.getRole())) {
            Optional<Student> studentOptional = studentService.findByUser(user);
            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                response.put("studentId", student.getId());
                response.put("school", student.getSchool());
                response.put("grade", student.getGrade());
                response.put("major", student.getMajor());
                response.put("address", student.getAddress());
            }
        } else if ("TEACHER".equals(user.getRole())) {
            Optional<Teacher> teacherOptional = teacherService.findByUser(user);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                response.put("teacherId", teacher.getId());
                response.put("school", teacher.getSchool());
                response.put("major", teacher.getMajor());
                response.put("education", teacher.getEducation());
                response.put("teachingExperience", teacher.getTeachingExperience());
                response.put("subject", teacher.getSubject());
                response.put("bio", teacher.getBio());
                response.put("pricePerHour", teacher.getPricePerHour());
                response.put("address", teacher.getAddress());
                response.put("rating", teacher.getRating());
                response.put("reviewCount", teacher.getReviewCount());
            }
        }
        
        return response;
    }
    
    public Map<String, Object> getStudentProfile(User user) {
        Optional<Student> studentOptional = studentService.findByUser(user);
        if (studentOptional.isEmpty()) {
            return null;
        }
        
        Student student = studentOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", student.getId());
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("school", student.getSchool());
        response.put("grade", student.getGrade());
        response.put("major", student.getMajor());
        response.put("address", student.getAddress());
        
        return response;
    }
    
    public Map<String, Object> getTeacherProfile(User user) {
        Optional<Teacher> teacherOptional = teacherService.findByUser(user);
        if (teacherOptional.isEmpty()) {
            return null;
        }
        
        Teacher teacher = teacherOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", teacher.getId());
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("name", user.getName());
        response.put("school", teacher.getSchool());
        response.put("major", teacher.getMajor());
        response.put("education", teacher.getEducation());
        response.put("teachingExperience", teacher.getTeachingExperience());
        response.put("subject", teacher.getSubject());
        response.put("bio", teacher.getBio());
        response.put("pricePerHour", teacher.getPricePerHour());
        response.put("address", teacher.getAddress());
        response.put("rating", teacher.getRating());
        response.put("reviewCount", teacher.getReviewCount());
        
        return response;
    }
    
    @Transactional
    public Map<String, Object> updateStudentProfile(User user, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            user.setName((String) profileData.get("name"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        updateUser(user);
        
        Optional<Student> studentOptional = studentService.findByUser(user);
        Student student;
        if (studentOptional.isPresent()) {
            student = studentOptional.get();
        } else {
            student = new Student();
            student.setUser(user);
        }
        
        if (profileData.containsKey("school")) {
            student.setSchool((String) profileData.get("school"));
        }
        if (profileData.containsKey("grade")) {
            student.setGrade((String) profileData.get("grade"));
        }
        if (profileData.containsKey("major")) {
            student.setMajor((String) profileData.get("major"));
        }
        if (profileData.containsKey("address")) {
            student.setAddress((String) profileData.get("address"));
        }
        
        studentService.updateStudent(student);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        return response;
    }
    
    @Transactional
    public Map<String, Object> updateTeacherProfile(User user, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            user.setName((String) profileData.get("name"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        updateUser(user);
        
        Optional<Teacher> teacherOptional = teacherService.findByUser(user);
        Teacher teacher;
        if (teacherOptional.isPresent()) {
            teacher = teacherOptional.get();
        } else {
            teacher = new Teacher();
            teacher.setUser(user);
        }
        
        if (profileData.containsKey("school")) {
            teacher.setSchool((String) profileData.get("school"));
        }
        if (profileData.containsKey("major")) {
            teacher.setMajor((String) profileData.get("major"));
        }
        if (profileData.containsKey("education")) {
            teacher.setEducation((String) profileData.get("education"));
        }
        if (profileData.containsKey("teachingExperience")) {
            teacher.setTeachingExperience((String) profileData.get("teachingExperience"));
        }
        if (profileData.containsKey("subject")) {
            teacher.setSubject((String) profileData.get("subject"));
        }
        if (profileData.containsKey("bio")) {
            teacher.setBio((String) profileData.get("bio"));
        }
        if (profileData.containsKey("pricePerHour")) {
            Object priceObj = profileData.get("pricePerHour");
            if (priceObj instanceof Number) {
                teacher.setPricePerHour(((Number) priceObj).doubleValue());
            }
        }
        if (profileData.containsKey("address")) {
            teacher.setAddress((String) profileData.get("address"));
        }
        
        teacherService.updateTeacher(teacher);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        return response;
    }
}