package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentRequest appointment;
    
    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private User evaluator;
    
    @ManyToOne
    @JoinColumn(name = "evaluated_id")
    private User evaluated;
    
    private Integer teachingQuality; // 1-5分
    private Integer attitude; // 1-5分
    private Integer satisfaction; // 1-5分
    private String comment;
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}