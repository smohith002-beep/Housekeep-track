package com.example.housekeeptrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "housekeepers")
public class Housekeeper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Housekeeper name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, length = 20)
    private String phone;

    @Email(message = "Valid email is required")
    @Column(length = 100)
    private String email;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HousekeeperStatus status = HousekeeperStatus.AVAILABLE;

    @Column(name = "current_task_id")
    private Long currentTaskId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "housekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CleaningTask> tasks = new ArrayList<>();

    public Housekeeper() {
    }

    public Housekeeper(String name, String phone, String email, HousekeeperStatus status) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = HousekeeperStatus.AVAILABLE;
        }
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HousekeeperStatus getStatus() {
        return status;
    }

    public void setStatus(HousekeeperStatus status) {
        this.status = status;
    }

    public Long getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(Long currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CleaningTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<CleaningTask> tasks) {
        this.tasks = tasks;
    }
}
