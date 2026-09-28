package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.CleaningTask;
import com.example.housekeeptrack.entity.CleaningTaskStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CleaningTaskDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long housekeeperId;
    private String housekeeperName;
    private CleaningTaskStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String notes;
    private Long turnaroundMinutes;

    public CleaningTaskDTO() {
    }

    public CleaningTaskDTO(CleaningTask task) {
        this.id = task.getId();
        if (task.getRoom() != null) {
            this.roomId = task.getRoom().getId();
            this.roomNumber = task.getRoom().getRoomNumber();
        }
        if (task.getHousekeeper() != null) {
            this.housekeeperId = task.getHousekeeper().getId();
            this.housekeeperName = task.getHousekeeper().getName();
        } else {
            this.housekeeperName = "Unassigned / Pending";
        }
        this.status = task.getStatus();
        this.assignedAt = task.getAssignedAt();
        this.startedAt = task.getStartedAt();
        this.completedAt = task.getCompletedAt();
        this.notes = task.getNotes();

        if (task.getAssignedAt() != null && task.getCompletedAt() != null) {
            this.turnaroundMinutes = ChronoUnit.MINUTES.between(task.getAssignedAt(), task.getCompletedAt());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getHousekeeperId() {
        return housekeeperId;
    }

    public void setHousekeeperId(Long housekeeperId) {
        this.housekeeperId = housekeeperId;
    }

    public String getHousekeeperName() {
        return housekeeperName;
    }

    public void setHousekeeperName(String housekeeperName) {
        this.housekeeperName = housekeeperName;
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

    public Long getTurnaroundMinutes() {
        return turnaroundMinutes;
    }

    public void setTurnaroundMinutes(Long turnaroundMinutes) {
        this.turnaroundMinutes = turnaroundMinutes;
    }
}
