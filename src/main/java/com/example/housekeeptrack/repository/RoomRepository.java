package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.Room;
import com.example.housekeeptrack.entity.RoomStatus;
import com.example.housekeeptrack.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByFloor(Integer floor);
    List<Room> findByRoomType(RoomType roomType);
    long countByStatus(RoomStatus status);
}
