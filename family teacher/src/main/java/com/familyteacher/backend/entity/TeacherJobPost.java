package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "teacher_job_posts")
public class TeacherJobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    private String title;
    private String description;
    private String subject;
    private Double pricePerHour;
    private String location;
    private String availability;
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