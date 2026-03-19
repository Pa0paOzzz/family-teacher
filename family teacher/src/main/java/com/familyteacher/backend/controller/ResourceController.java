package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.service.TeacherJobPostService;
import com.familyteacher.backend.service.StudentTutoringRequestService;
import com.familyteacher.backend.service.TeacherService;
import com.familyteacher.backend.service.StudentService;
import com.familyteacher.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    @Autowired
    private TeacherJobPostService teacherJobPostService;
    
    @Autowired
    private StudentTutoringRequestService studentTutoringRequestService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/teacher/job-post")
    public Map<String, Object> createJobPost(@RequestBody Map<String, Object> jobPostData, HttpServletRequest request) {
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
        
        Teacher teacher = teacherService.findByUser(user).orElse(null);
        if (teacher == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Teacher profile not found");
            return error;
        }
        
        return teacherJobPostService.createJobPost(teacher, jobPostData);
    }
    
    @GetMapping("/teacher/job-posts")
    public List<TeacherJobPost> getAllActiveJobPosts() {
        return teacherJobPostService.getAllActiveJobPosts();
    }
    
    @GetMapping("/teacher/my-job-posts")
    public List<TeacherJobPost> getMyJobPosts(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }
        
        Teacher teacher = teacherService.findByUser(user).orElse(null);
        if (teacher == null) {
            return List.of();
        }
        
        return teacherJobPostService.getJobPostsByTeacher(teacher);
    }
    
    @GetMapping("/teacher/job-posts/search")
    public List<TeacherJobPost> searchJobPosts(@RequestParam String subject) {
        return teacherJobPostService.searchJobPostsBySubject(subject);
    }
    
    @PutMapping("/teacher/job-post")
    public Map<String, Object> updateJobPost(@RequestBody Map<String, Object> jobPostData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        if (!jobPostData.containsKey("id")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Job post ID is required");
            return error;
        }
        
        Long jobPostId = ((Number) jobPostData.get("id")).longValue();
        return teacherJobPostService.updateJobPost(jobPostId, jobPostData);
    }
    
    @PutMapping("/teacher/job-post/{id}/deactivate")
    public Map<String, Object> deactivateJobPost(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        return teacherJobPostService.deactivateJobPostResponse(id);
    }
    
    @PostMapping("/tutoring-requests")
    public Map<String, Object> createTutoringRequest(@RequestBody Map<String, Object> requestData, HttpServletRequest request) {
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
        
        Student student = studentService.findByUser(user).orElse(null);
        if (student == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Student profile not found");
            return error;
        }
        
        return studentTutoringRequestService.createTutoringRequestFromData(student, requestData);
    }
    
    @GetMapping("/tutoring-requests")
    public List<StudentTutoringRequest> getAllActiveRequests() {
        return studentTutoringRequestService.getAllActiveRequests();
    }
    
    @GetMapping("/my-tutoring-requests")
    public List<StudentTutoringRequest> getMyTutoringRequests(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }
        
        Student student = studentService.findByUser(user).orElse(null);
        if (student == null) {
            return List.of();
        }
        
        return studentTutoringRequestService.getRequestsByStudent(student);
    }
    
    @GetMapping("/tutoring-requests/search")
    public List<StudentTutoringRequest> searchRequests(@RequestParam String subject) {
        return studentTutoringRequestService.searchRequestsBySubject(subject);
    }
    
    @PutMapping("/tutoring-request")
    public Map<String, Object> updateTutoringRequest(@RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        if (!requestData.containsKey("id")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Request ID is required");
            return error;
        }
        
        Long requestId = ((Number) requestData.get("id")).longValue();
        return studentTutoringRequestService.updateTutoringRequestFromData(requestId, requestData);
    }
    
    @PutMapping("/tutoring-request/{id}/deactivate")
    public Map<String, Object> deactivateTutoringRequest(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        return studentTutoringRequestService.deactivateTutoringRequestResponse(id);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
