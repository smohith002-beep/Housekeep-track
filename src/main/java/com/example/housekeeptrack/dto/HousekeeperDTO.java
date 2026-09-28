package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.Housekeeper;
import com.example.housekeeptrack.entity.HousekeeperStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class HousekeeperDTO {
    private Long id;

    @NotBlank(message = "Housekeeper name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Valid email is required")
    private String email;

    private HousekeeperStatus status;
    private Long currentTaskId;
    private String currentRoomNumber;
    private long completedTasksCount;

    public HousekeeperDTO() {
    }

    public HousekeeperDTO(Housekeeper housekeeper) {
        this.id = housekeeper.getId();
        this.name = housekeeper.getName();
        this.phone = housekeeper.getPhone();
        this.email = housekeeper.getEmail();
        this.status = housekeeper.getStatus();
        this.currentTaskId = housekeeper.getCurrentTaskId();
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

    public String getCurrentRoomNumber() {
        return currentRoomNumber;
    }

    public void setCurrentRoomNumber(String currentRoomNumber) {
        this.currentRoomNumber = currentRoomNumber;
    }

    public long getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void setCompletedTasksCount(long completedTasksCount) {
        this.completedTasksCount = completedTasksCount;
    }
}
