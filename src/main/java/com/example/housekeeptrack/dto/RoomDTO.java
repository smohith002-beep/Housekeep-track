package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.Room;
import com.example.housekeeptrack.entity.RoomStatus;
import com.example.housekeeptrack.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomDTO {
    private Long id;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Floor is required")
    private Integer floor;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    private RoomStatus status;
    private Integer capacity = 2;
    private Double pricePerNight = 18000.0;
    private String description;
    private String imageUrl;

    public RoomDTO() {
    }

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.floor = room.getFloor();
        this.roomType = room.getRoomType();
        this.status = room.getStatus();
        this.capacity = room.getCapacity();
        this.pricePerNight = room.getPricePerNight();
        this.description = room.getDescription();
        this.imageUrl = room.getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
