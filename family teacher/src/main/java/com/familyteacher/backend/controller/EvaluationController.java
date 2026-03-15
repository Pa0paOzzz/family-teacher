package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.Teacher;
import com.familyteacher.backend.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;
    
    @PostMapping("/create")
    public Evaluation createEvaluation(@RequestBody Evaluation evaluation) {
        return evaluationService.createEvaluation(evaluation);
    }
    
    @GetMapping("/by-appointment/{appointmentId}")
    public List<Evaluation> getEvaluationsByAppointment(@PathVariable Long appointmentId) {
        // 这里需要根据appointmentId获取AppointmentRequest对象，简化处理
        com.familyteacher.backend.entity.AppointmentRequest appointment = new com.familyteacher.backend.entity.AppointmentRequest();
        appointment.setId(appointmentId);
        return evaluationService.getEvaluationsByAppointment(appointment);
    }
    
    @GetMapping("/by-evaluated/{userId}")
    public List<Evaluation> getEvaluationsByEvaluated(@PathVariable Long userId) {
        // 这里需要根据userId获取User对象，简化处理
        com.familyteacher.backend.entity.User user = new com.familyteacher.backend.entity.User();
        user.setId(userId);
        return evaluationService.getEvaluationsByEvaluated(user);
    }
    
    @GetMapping("/credit-score/{teacherId}")
    public int getCreditScore(@PathVariable Long teacherId) {
        // 这里需要根据teacherId获取Teacher对象，简化处理
        Teacher teacher = new Teacher();
        com.familyteacher.backend.entity.User user = new com.familyteacher.backend.entity.User();
        user.setId(teacherId);
        teacher.setUser(user);
        return evaluationService.calculateCreditScore(teacher);
    }
}