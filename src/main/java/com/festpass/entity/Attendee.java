package com.festpass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendees")
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be empty")
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank(message = "Phone must not be empty")
    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 150)
    private String collegeName;

    @NotBlank(message = "Student ID must not be empty")
    @Column(nullable = false, length = 50)
    private String studentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Attendee() {
    }

    public Attendee(String name, String email, String phone, String collegeName, String studentId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.collegeName = collegeName;
        this.studentId = studentId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
