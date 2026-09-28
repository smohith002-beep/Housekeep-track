package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.Booking;
import com.example.housekeeptrack.entity.BookingStatus;
import com.example.housekeeptrack.entity.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long id;
    private Long guestId;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus bookingStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(Booking booking) {
        this.id = booking.getId();
        if (booking.getGuest() != null) {
            this.guestId = booking.getGuest().getId();
            this.guestName = booking.getGuest().getName();
            this.guestPhone = booking.getGuest().getPhone();
            this.guestEmail = booking.getGuest().getEmail();
        }
        if (booking.getRoom() != null) {
            this.roomId = booking.getRoom().getId();
            this.roomNumber = booking.getRoom().getRoomNumber();
            this.roomType = booking.getRoom().getRoomType();
        }
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.bookingStatus = booking.getBookingStatus();
        this.totalAmount = booking.getTotalAmount();
        this.createdAt = booking.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
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

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
