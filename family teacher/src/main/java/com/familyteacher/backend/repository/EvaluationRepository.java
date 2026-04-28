package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.Evaluation;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByAppointment(AppointmentRequest appointment);
    List<Evaluation> findByEvaluated(User evaluated);
    List<Evaluation> findByEvaluator(User evaluator);
    Optional<Evaluation> findByAppointmentAndEvaluator(AppointmentRequest appointment, User evaluator);
}