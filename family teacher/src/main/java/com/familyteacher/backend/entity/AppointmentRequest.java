package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "appointment_requests")
public class AppointmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    private String subject;
    private Date requestedDate;
    private String requestedTime;
    private String location;
    private Double pricePerHour;
    private Integer durationHours;
    private String status; // PENDING, ACCEPTED, REJECTED, COMPLETED, LONG_TERM_CONFIRMED
    private String appointmentType; // TRIAL_INTERVIEW
    private Boolean studentConfirmedLongTerm;
    private Boolean teacherConfirmedLongTerm;
    private Date longTermConfirmedAt;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        status = "PENDING";
        appointmentType = "TRIAL_INTERVIEW";
        if (studentConfirmedLongTerm == null) {
            studentConfirmedLongTerm = false;
        }
        if (teacherConfirmedLongTerm == null) {
            teacherConfirmedLongTerm = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
