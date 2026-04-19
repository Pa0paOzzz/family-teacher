package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "student_tutoring_requests")
public class StudentTutoringRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    private String title;
    private String description;
    private String subject;
    private Double budgetPerHour;
    private String location;
    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String locationFormatted;
    private Double locationLongitude;
    private Double locationLatitude;
    private String preferredTime;
    private Date createdAt;
    private Date updatedAt;
    private Boolean active;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        active = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}