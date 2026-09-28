package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;

public class RoomStatusUpdateDTO {
    @NotNull(message = "New status is required")
    private RoomStatus status;

    public RoomStatusUpdateDTO() {
    }

    public RoomStatusUpdateDTO(RoomStatus status) {
        this.status = status;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
