package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.repository.EvaluationRepository;
import com.familyteacher.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    public Evaluation createEvaluation(Evaluation evaluation) {
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        
        // 更新被评价家教的评分
        updateTeacherRating(evaluation.getEvaluated());
        
        return savedEvaluation;
    }
    
    public List<Evaluation> getEvaluationsByAppointment(AppointmentRequest appointment) {
        return evaluationRepository.findByAppointment(appointment);
    }
    
    public List<Evaluation> getEvaluationsByEvaluated(User evaluated) {
        return evaluationRepository.findByEvaluated(evaluated);
    }
    
    public List<Evaluation> getEvaluationsByEvaluator(User evaluator) {
        return evaluationRepository.findByEvaluator(evaluator);
    }

    public Optional<Evaluation> getEvaluationByAppointmentAndEvaluator(AppointmentRequest appointment, User evaluator) {
        return evaluationRepository.findByAppointmentAndEvaluator(appointment, evaluator);
    }

    // 更新家教的评分
    private void updateTeacherRating(User user) {
        if (user != null && "TEACHER".equals(user.getRole())) {
            List<Evaluation> evaluations = evaluationRepository.findByEvaluated(user);
            if (!evaluations.isEmpty()) {
                int totalScore = 0;
                for (Evaluation eval : evaluations) {
                    int avgScore = (eval.getTeachingQuality() + eval.getAttitude() + eval.getSatisfaction()) / 3;
                    totalScore += avgScore;
                }
                int averageRating = totalScore / evaluations.size();
                
                Teacher teacher = teacherRepository.findByUser(user).orElse(null);
                if (teacher != null) {
                    teacher.setRating(averageRating);
                    teacher.setReviewCount(evaluations.size());
                    teacherRepository.save(teacher);
                }
            }
        }
    }
    
    // 计算家教的信用积分
    public int calculateCreditScore(Teacher teacher) {
        List<Evaluation> evaluations = evaluationRepository.findByEvaluated(teacher.getUser());
        int creditScore = 0;
        
        // 基础积分：每个订单10分
        creditScore += teacher.getReviewCount() * 10;
        
        // 评分加成：根据平均评分
        if (teacher.getRating() != null) {
            creditScore += teacher.getRating() * 5;
        }
        
        // 活跃度加成：假设每完成一个订单加5分
        creditScore += teacher.getReviewCount() * 5;
        
        return creditScore;
    }
}