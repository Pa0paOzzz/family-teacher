package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;
    
    // 推荐家教给学生
    @PostMapping("/teachers-for-student")
    public List<TeacherJobPost> recommendTeachersForStudent(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendTeachersForStudent(request.getStudent(), request.getTutoringRequest());
    }
    
    // 推荐学生给家教
    @PostMapping("/students-for-teacher")
    public List<StudentTutoringRequest> recommendStudentsForTeacher(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendStudentsForTeacher(request.getTeacher(), request.getJobPost());
    }
    
    // 推荐请求参数类
    public static class RecommendationRequest {
        private Student student;
        private Teacher teacher;
        private StudentTutoringRequest tutoringRequest;
        private TeacherJobPost jobPost;
        
        // getters and setters
        public Student getStudent() { return student; }
        public void setStudent(Student student) { this.student = student; }
        public Teacher getTeacher() { return teacher; }
        public void setTeacher(Teacher teacher) { this.teacher = teacher; }
        public StudentTutoringRequest getTutoringRequest() { return tutoringRequest; }
        public void setTutoringRequest(StudentTutoringRequest tutoringRequest) { this.tutoringRequest = tutoringRequest; }
        public TeacherJobPost getJobPost() { return jobPost; }
        public void setJobPost(TeacherJobPost jobPost) { this.jobPost = jobPost; }
    }
}