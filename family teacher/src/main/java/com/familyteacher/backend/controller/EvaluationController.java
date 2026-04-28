package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.service.EvaluationService;
import com.familyteacher.backend.service.AppointmentService;
import com.familyteacher.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Map<String, Object> createEvaluation(@RequestBody Map<String, Object> evaluationData, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = extractToken(request);
            if (token == null) {
                response.put("success", false);
                response.put("error", "No token provided");
                return response;
            }

            User evaluator = userService.getUserFromToken(token);
            if (evaluator == null) {
                response.put("success", false);
                response.put("error", "User not found");
                return response;
            }

            Long appointmentId = ((Number) evaluationData.get("appointmentId")).longValue();
            AppointmentRequest appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                response.put("success", false);
                response.put("error", "Appointment not found");
                return response;
            }
            if (!isParticipant(appointment, evaluator)) {
                response.put("success", false);
                response.put("error", "Permission denied");
                return response;
            }
            if (!appointmentService.canEvaluate(appointment)) {
                response.put("success", false);
                response.put("error", "Evaluation is only available after long-term teaching is completed");
                return response;
            }

            Long evaluatedId = ((Number) evaluationData.get("evaluatedId")).longValue();
            User evaluated = userService.getUserById(evaluatedId);
            if (evaluated == null) {
                response.put("success", false);
                response.put("error", "Evaluated user not found");
                return response;
            }
            if (Objects.equals(evaluator.getId(), evaluated.getId())) {
                response.put("success", false);
                response.put("error", "You cannot evaluate yourself");
                return response;
            }
            User counterpart = getCounterpartUser(appointment, evaluator);
            if (counterpart == null || !Objects.equals(counterpart.getId(), evaluated.getId())) {
                response.put("success", false);
                response.put("error", "Evaluated user must be the other participant in the appointment");
                return response;
            }
            if (evaluationService.getEvaluationByAppointmentAndEvaluator(appointment, evaluator).isPresent()) {
                response.put("success", false);
                response.put("error", "You have already submitted an evaluation for this appointment");
                return response;
            }

            Evaluation evaluation = new Evaluation();
            evaluation.setAppointment(appointment);
            evaluation.setEvaluator(evaluator);
            evaluation.setEvaluated(evaluated);
            evaluation.setTeachingQuality(((Number) evaluationData.get("teachingQuality")).intValue());
            evaluation.setAttitude(((Number) evaluationData.get("attitude")).intValue());
            evaluation.setSatisfaction(((Number) evaluationData.get("satisfaction")).intValue());
            evaluation.setComment((String) evaluationData.get("comment"));

            Evaluation savedEvaluation = evaluationService.createEvaluation(evaluation);

            response.put("success", true);
            response.put("message", "Evaluation created successfully");
            response.put("id", savedEvaluation.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to create evaluation: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @GetMapping("/my-evaluations")
    public List<Map<String, Object>> getMyEvaluations(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }

        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }

        List<Evaluation> evaluations = evaluationService.getEvaluationsByEvaluator(user);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Evaluation evaluation : evaluations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", evaluation.getId());
            item.put("teachingQuality", evaluation.getTeachingQuality());
            item.put("attitude", evaluation.getAttitude());
            item.put("satisfaction", evaluation.getSatisfaction());
            item.put("comment", evaluation.getComment());
            item.put("createdAt", evaluation.getCreatedAt());

            if (evaluation.getEvaluated() != null) {
                item.put("evaluatedName", evaluation.getEvaluated().getName());
                item.put("evaluatedId", evaluation.getEvaluated().getId());
            } else {
                item.put("evaluatedName", "未知");
                item.put("evaluatedId", 0);
            }

            if (evaluation.getAppointment() != null) {
                item.put("appointmentId", evaluation.getAppointment().getId());
                item.put("subject", evaluation.getAppointment().getSubject());
                item.put("requestedDate", evaluation.getAppointment().getRequestedDate());
            } else {
                item.put("appointmentId", 0);
                item.put("subject", "未知");
            }

            result.add(item);
        }

        return result;
    }

    @GetMapping("/received-evaluations")
    public List<Map<String, Object>> getReceivedEvaluations(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }

        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }

        List<Evaluation> evaluations = evaluationService.getEvaluationsByEvaluated(user);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Evaluation evaluation : evaluations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", evaluation.getId());
            item.put("teachingQuality", evaluation.getTeachingQuality());
            item.put("attitude", evaluation.getAttitude());
            item.put("satisfaction", evaluation.getSatisfaction());
            item.put("comment", evaluation.getComment());
            item.put("createdAt", evaluation.getCreatedAt());

            if (evaluation.getEvaluator() != null) {
                item.put("evaluatorName", evaluation.getEvaluator().getName());
                item.put("evaluatorId", evaluation.getEvaluator().getId());
            } else {
                item.put("evaluatorName", "未知");
                item.put("evaluatorId", 0);
            }

            if (evaluation.getAppointment() != null) {
                item.put("appointmentId", evaluation.getAppointment().getId());
                item.put("subject", evaluation.getAppointment().getSubject());
                item.put("requestedDate", evaluation.getAppointment().getRequestedDate());
            } else {
                item.put("appointmentId", 0);
                item.put("subject", "未知");
            }

            result.add(item);
        }

        return result;
    }

    @GetMapping("/check")
    public Map<String, Object> checkEvaluation(@RequestParam Long appointmentId, HttpServletRequest request) {
        String token = extractToken(request);
        Map<String, Object> response = new HashMap<>();
        response.put("hasEvaluated", false);
        response.put("canEvaluate", false);

        if (token == null) {
            return response;
        }

        User user = userService.getUserFromToken(token);
        if (user == null) {
            return response;
        }

        AppointmentRequest appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null || !isParticipant(appointment, user)) {
            return response;
        }

        boolean hasEvaluated = evaluationService.getEvaluationByAppointmentAndEvaluator(appointment, user).isPresent();
        response.put("hasEvaluated", hasEvaluated);
        response.put("canEvaluate", appointmentService.canEvaluate(appointment) && !hasEvaluated);
        return response;
    }

    private boolean isParticipant(AppointmentRequest appointment, User user) {
        if (appointment == null || user == null) {
            return false;
        }
        return appointment.getStudent() != null
                && appointment.getStudent().getUser() != null
                && Objects.equals(appointment.getStudent().getUser().getId(), user.getId())
            || appointment.getTeacher() != null
                && appointment.getTeacher().getUser() != null
                && Objects.equals(appointment.getTeacher().getUser().getId(), user.getId());
    }

    private User getCounterpartUser(AppointmentRequest appointment, User user) {
        if (appointment == null || user == null) {
            return null;
        }
        if (appointment.getStudent() != null
                && appointment.getStudent().getUser() != null
                && Objects.equals(appointment.getStudent().getUser().getId(), user.getId())) {
            return appointment.getTeacher() == null ? null : appointment.getTeacher().getUser();
        }
        if (appointment.getTeacher() != null
                && appointment.getTeacher().getUser() != null
                && Objects.equals(appointment.getTeacher().getUser().getId(), user.getId())) {
            return appointment.getStudent() == null ? null : appointment.getStudent().getUser();
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
