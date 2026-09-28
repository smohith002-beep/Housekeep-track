package com.example.housekeeptrack.service;

import com.example.housekeeptrack.entity.Room;
import com.example.housekeeptrack.entity.RoomStatus;
import com.example.housekeeptrack.exception.InvalidRoomStatusTransitionException;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomStatusService {

    private final RoomRepository roomRepository;
    private final AuditLogService auditLogService;

    public RoomStatusService(RoomRepository roomRepository, AuditLogService auditLogService) {
        this.roomRepository = roomRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Room changeRoomStatus(Long roomId, RoomStatus newStatus, boolean isCheckout, String changedBy) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        RoomStatus currentStatus = room.getStatus();

        if (currentStatus == newStatus) {
            return room; // No change needed
        }

        boolean allowed = false;

        // Transition logic based on Business Rules:
        // DIRTY -> CLEANING
        // CLEANING -> INSPECTED
        // INSPECTED -> READY
        // INSPECTED -> CLEANING (inspection failed)
        // READY -> DIRTY (only when guest checks out)
        if (currentStatus == RoomStatus.DIRTY && newStatus == RoomStatus.CLEANING) {
            allowed = true;
        } else if (currentStatus == RoomStatus.CLEANING && newStatus == RoomStatus.INSPECTED) {
            allowed = true;
        } else if (currentStatus == RoomStatus.INSPECTED && newStatus == RoomStatus.READY) {
            allowed = true;
        } else if (currentStatus == RoomStatus.INSPECTED && newStatus == RoomStatus.CLEANING) {
            allowed = true;
        } else if (currentStatus == RoomStatus.READY && newStatus == RoomStatus.DIRTY) {
            if (isCheckout) {
                allowed = true;
            } else {
                throw new InvalidRoomStatusTransitionException(
                        "Invalid room status transition: READY room can only become DIRTY upon guest checkout.");
            }
        }

        if (!allowed) {
            throw new InvalidRoomStatusTransitionException(
                    "Invalid room status transition: " + currentStatus + " cannot directly change to " + newStatus + ".");
        }

        room.setStatus(newStatus);
        Room updatedRoom = roomRepository.save(room);

        auditLogService.log(
                "ROOM",
                room.getId(),
                "ROOM_STATUS_CHANGED",
                currentStatus.name(),
                newStatus.name(),
                changedBy != null ? changedBy : "SYSTEM"
        );

        return updatedRoom;
    }

    @Transactional
    public Room changeRoomStatus(Long roomId, RoomStatus newStatus, String changedBy) {
        return changeRoomStatus(roomId, newStatus, false, changedBy);
    }
}
