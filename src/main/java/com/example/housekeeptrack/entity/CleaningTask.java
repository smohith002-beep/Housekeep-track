package com.example.housekeeptrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "cleaning_tasks")
public class CleaningTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "housekeeper_id")
    private Housekeeper housekeeper;

    @NotNull(message = "Task status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CleaningTaskStatus status = CleaningTaskStatus.ASSIGNED;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String notes;

    public CleaningTask() {
    }

    public CleaningTask(Room room, Housekeeper housekeeper, CleaningTaskStatus status, String notes) {
        this.room = room;
        this.housekeeper = housekeeper;
        this.status = status;
        this.notes = notes;
        this.assignedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = CleaningTaskStatus.ASSIGNED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Housekeeper getHousekeeper() {
        return housekeeper;
    }

    public void setHousekeeper(Housekeeper housekeeper) {
        this.housekeeper = housekeeper;
    }

    public CleaningTaskStatus getStatus() {
        return status;
    }

    public void setStatus(CleaningTaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
