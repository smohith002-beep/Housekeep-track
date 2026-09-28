package com.example.housekeeptrack.dto;

public class CheckoutResponseDTO {
    private String message;
    private String roomNumber;
    private String roomStatus;
    private Long taskId;
    private String housekeeper;
    private String taskStatus;

    public CheckoutResponseDTO() {
    }

    public CheckoutResponseDTO(String message, String roomNumber, String roomStatus, Long taskId, String housekeeper, String taskStatus) {
        this.message = message;
        this.roomNumber = roomNumber;
        this.roomStatus = roomStatus;
        this.taskId = taskId;
        this.housekeeper = housekeeper;
        this.taskStatus = taskStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getHousekeeper() {
        return housekeeper;
    }

    public void setHousekeeper(String housekeeper) {
        this.housekeeper = housekeeper;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
