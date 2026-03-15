package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.service.TeacherJobPostService;
import com.familyteacher.backend.service.StudentTutoringRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    @Autowired
    private TeacherJobPostService teacherJobPostService;
    
    @Autowired
    private StudentTutoringRequestService studentTutoringRequestService;
    
    // 家教求职信息相关API
    @PostMapping("/teacher/job-post")
    public TeacherJobPost createJobPost(@RequestBody TeacherJobPost jobPost) {
        return teacherJobPostService.createJobPost(jobPost);
    }
    
    @GetMapping("/teacher/job-posts")
    public List<TeacherJobPost> getAllActiveJobPosts() {
        return teacherJobPostService.getAllActiveJobPosts();
    }
    
    @GetMapping("/teacher/job-posts/search")
    public List<TeacherJobPost> searchJobPosts(@RequestParam String subject) {
        return teacherJobPostService.searchJobPostsBySubject(subject);
    }
    
    @PutMapping("/teacher/job-post")
    public TeacherJobPost updateJobPost(@RequestBody TeacherJobPost jobPost) {
        return teacherJobPostService.updateJobPost(jobPost);
    }
    
    @PutMapping("/teacher/job-post/{id}/deactivate")
    public void deactivateJobPost(@PathVariable Long id) {
        teacherJobPostService.deactivateJobPost(id);
    }
    
    // 学生家教需求相关API
    @PostMapping("/student/tutoring-request")
    public StudentTutoringRequest createTutoringRequest(@RequestBody StudentTutoringRequest request) {
        return studentTutoringRequestService.createTutoringRequest(request);
    }
    
    @GetMapping("/student/tutoring-requests")
    public List<StudentTutoringRequest> getAllActiveRequests() {
        return studentTutoringRequestService.getAllActiveRequests();
    }
    
    @GetMapping("/student/tutoring-requests/search")
    public List<StudentTutoringRequest> searchRequests(@RequestParam String subject) {
        return studentTutoringRequestService.searchRequestsBySubject(subject);
    }
    
    @PutMapping("/student/tutoring-request")
    public StudentTutoringRequest updateTutoringRequest(@RequestBody StudentTutoringRequest request) {
        return studentTutoringRequestService.updateTutoringRequest(request);
    }
    
    @PutMapping("/student/tutoring-request/{id}/deactivate")
    public void deactivateTutoringRequest(@PathVariable Long id) {
        studentTutoringRequestService.deactivateTutoringRequest(id);
    }
}