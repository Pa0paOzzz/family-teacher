package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String school;
    private String grade;
    private String major;
    private String address;
    private String addressProvince;
    private String addressCity;
    private String addressDistrict;
    private String addressFormatted;
    private Double addressLongitude;
    private Double addressLatitude;
}