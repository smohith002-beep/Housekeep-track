package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.Booking;
import com.example.housekeeptrack.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookingStatus(BookingStatus status);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findAllByOrderByCreatedAtDesc();
}
