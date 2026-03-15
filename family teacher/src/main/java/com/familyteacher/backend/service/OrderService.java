package com.familyteacher.backend.service;

import com.familyteacher.backend.entity.Order;
import com.familyteacher.backend.entity.AppointmentRequest;
import com.familyteacher.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public Order getOrderByAppointment(AppointmentRequest appointment) {
        return orderRepository.findByAppointment(appointment).orElse(null);
    }
    
    public Order updateOrderStatus(Long id, String paymentStatus) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            return orderRepository.save(order);
        }
        return null;
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}