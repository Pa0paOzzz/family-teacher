package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentRequest appointment;
    
    private Double totalAmount;
    private String paymentStatus; // PENDING, PAID, REFUNDED
    private String transactionId;
    private Date createdAt;
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        paymentStatus = "PENDING";
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}