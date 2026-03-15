package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.TeacherJobPost;
import com.familyteacher.backend.entity.StudentTutoringRequest;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.entity.Student;
import com.familyteacher.backend.repository.TeacherJobPostRepository;
import com.familyteacher.backend.repository.StudentTutoringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private TeacherJobPostRepository teacherJobPostRepository;
    
    @Autowired
    private StudentTutoringRequestRepository studentTutoringRequestRepository;
    
    // 推荐家教给学生
    public List<TeacherJobPost> recommendTeachersForStudent(Student student, StudentTutoringRequest request) {
        List<TeacherJobPost> allActiveJobPosts = teacherJobPostRepository.findByActiveTrue();
        
        // 计算每个家教职位与学生需求的匹配度
        Map<TeacherJobPost, Double> scores = new HashMap<>();
        for (TeacherJobPost jobPost : allActiveJobPosts) {
            double score = calculateTeacherMatchScore(jobPost, request);
            scores.put(jobPost, score);
        }
        
        // 按匹配度排序，返回前10个
        return scores.entrySet().stream()
                .sorted(Map.Entry.<TeacherJobPost, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    // 推荐学生给家教
    public List<StudentTutoringRequest> recommendStudentsForTeacher(Teacher teacher, TeacherJobPost jobPost) {
        List<StudentTutoringRequest> allActiveRequests = studentTutoringRequestRepository.findByActiveTrue();
        
        // 计算每个学生需求与家教职位的匹配度
        Map<StudentTutoringRequest, Double> scores = new HashMap<>();
        for (StudentTutoringRequest request : allActiveRequests) {
            double score = calculateStudentMatchScore(request, jobPost);
            scores.put(request, score);
        }
        
        // 按匹配度排序，返回前10个
        return scores.entrySet().stream()
                .sorted(Map.Entry.<StudentTutoringRequest, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    // 计算家教职位与学生需求的匹配度
    private double calculateTeacherMatchScore(TeacherJobPost jobPost, StudentTutoringRequest request) {
        double score = 0.0;
        
        // 学科匹配（权重40%）
        if (jobPost.getSubject().equalsIgnoreCase(request.getSubject())) {
            score += 40.0;
        } else if (jobPost.getSubject().toLowerCase().contains(request.getSubject().toLowerCase())) {
            score += 20.0;
        }
        
        // 价格匹配（权重20%）
        double priceDifference = Math.abs(jobPost.getPricePerHour() - request.getBudgetPerHour());
        double priceRatio = Math.max(0, 1 - priceDifference / request.getBudgetPerHour());
        score += priceRatio * 20.0;
        
        // 位置匹配（权重20%）
        if (jobPost.getLocation().equalsIgnoreCase(request.getLocation())) {
            score += 20.0;
        }
        
        // 家教评分（权重20%）
        Teacher teacher = jobPost.getTeacher();
        if (teacher != null && teacher.getRating() != null) {
            score += (teacher.getRating() / 5.0) * 20.0;
        }
        
        return score;
    }
    
    // 计算学生需求与家教职位的匹配度
    private double calculateStudentMatchScore(StudentTutoringRequest request, TeacherJobPost jobPost) {
        double score = 0.0;
        
        // 学科匹配（权重40%）
        if (request.getSubject().equalsIgnoreCase(jobPost.getSubject())) {
            score += 40.0;
        } else if (request.getSubject().toLowerCase().contains(jobPost.getSubject().toLowerCase())) {
            score += 20.0;
        }
        
        // 价格匹配（权重20%）
        double priceDifference = Math.abs(request.getBudgetPerHour() - jobPost.getPricePerHour());
        double priceRatio = Math.max(0, 1 - priceDifference / jobPost.getPricePerHour());
        score += priceRatio * 20.0;
        
        // 位置匹配（权重20%）
        if (request.getLocation().equalsIgnoreCase(jobPost.getLocation())) {
            score += 20.0;
        }
        
        // 活跃度（权重20%）
        long daysSinceCreation = (System.currentTimeMillis() - request.getCreatedAt().getTime()) / (1000 * 60 * 60 * 24);
        if (daysSinceCreation <= 7) {
            score += 20.0;
        } else if (daysSinceCreation <= 30) {
            score += 10.0;
        }
        
        return score;
    }
}