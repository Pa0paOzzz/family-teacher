package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String school;
    private String major;
    private String education;
    private String teachingExperience;
    private String subject;
    private String bio;
    private Double pricePerHour;
    private String address;
    private Integer rating;
    private Integer reviewCount;
}