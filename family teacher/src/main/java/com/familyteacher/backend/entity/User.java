package com.familyteacher.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role; // STUDENT, TEACHER, ADMIN
    private String name;
    private Boolean enabled;
    private Boolean deleted;
    private Date createdAt;
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        if (enabled == null) {
            enabled = true;
        }
        if (deleted == null) {
            deleted = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
