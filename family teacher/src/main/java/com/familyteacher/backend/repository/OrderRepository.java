package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.entity.AppointmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByAppointment(AppointmentRequest appointment);
}