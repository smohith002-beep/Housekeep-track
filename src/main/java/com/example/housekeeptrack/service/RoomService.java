package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.RoomDTO;
import com.example.housekeeptrack.entity.Room;
import com.example.housekeeptrack.entity.RoomStatus;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.BookingRepository;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.InspectionRepository;
import com.example.housekeeptrack.repository.RoomRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final InspectionRepository inspectionRepository;
    private final RoomStatusService roomStatusService;
    private final AuditLogService auditLogService;

    public RoomService(RoomRepository roomRepository,
                       BookingRepository bookingRepository,
                       CleaningTaskRepository cleaningTaskRepository,
                       InspectionRepository inspectionRepository,
                       RoomStatusService roomStatusService,
                       AuditLogService auditLogService) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.inspectionRepository = inspectionRepository;
        this.roomStatusService = roomStatusService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " not found"));
        return new RoomDTO(room);
    }

    @Transactional(readOnly = true)
    public Room getRoomEntity(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status).stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDTO createRoom(RoomDTO dto) {
        roomRepository.findByRoomNumber(dto.getRoomNumber()).ifPresent(r -> {
            throw new IllegalArgumentException("Room number " + dto.getRoomNumber() + " already exists.");
        });

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setFloor(dto.getFloor());
        room.setRoomType(dto.getRoomType());
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : RoomStatus.READY);
        room.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 2);
        room.setPricePerNight(dto.getPricePerNight() != null ? dto.getPricePerNight() : 18000.0);
        room.setDescription(dto.getDescription() != null ? dto.getDescription() : "Luxury chamber at HouseKeepTrack Hotel.");
        room.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80");

        Room saved = roomRepository.save(room);
        auditLogService.log("ROOM", saved.getId(), "ROOM_CREATED", null, saved.getRoomNumber(), "ADMIN");
        return new RoomDTO(saved);
    }

    @Transactional
    public RoomDTO updateRoom(Long id, RoomDTO dto) {
        Room room = getRoomEntity(id);

        if (dto.getRoomNumber() != null && !room.getRoomNumber().equals(dto.getRoomNumber())) {
            roomRepository.findByRoomNumber(dto.getRoomNumber()).ifPresent(r -> {
                throw new IllegalArgumentException("Room number " + dto.getRoomNumber() + " already exists.");
            });
            room.setRoomNumber(dto.getRoomNumber());
        }

        if (dto.getFloor() != null) room.setFloor(dto.getFloor());
        if (dto.getRoomType() != null) room.setRoomType(dto.getRoomType());
        if (dto.getStatus() != null) room.setStatus(dto.getStatus());
        if (dto.getCapacity() != null) room.setCapacity(dto.getCapacity());
        if (dto.getPricePerNight() != null) room.setPricePerNight(dto.getPricePerNight());
        if (dto.getDescription() != null) room.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) room.setImageUrl(dto.getImageUrl());

        Room updated = roomRepository.save(room);
        auditLogService.log("ROOM", updated.getId(), "ROOM_UPDATED", null, updated.getRoomNumber(), "ADMIN");
        return new RoomDTO(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomEntity(id);

        // Check foreign key dependencies before deletion
        long bookingCount = bookingRepository.findByRoomId(id).size();
        long taskCount = cleaningTaskRepository.findByRoomId(id).size();
        long inspectionCount = inspectionRepository.findByRoomId(id).size();

        if (bookingCount > 0 || taskCount > 0 || inspectionCount > 0) {
            throw new DataIntegrityViolationException(
                    "Cannot delete Room " + room.getRoomNumber() + " because it has " +
                    (bookingCount > 0 ? bookingCount + " associated booking(s) " : "") +
                    (taskCount > 0 ? taskCount + " cleaning task(s) " : "") +
                    (inspectionCount > 0 ? inspectionCount + " inspection(s)" : "") +
                    ". Please remove dependencies first.");
        }

        roomRepository.delete(room);
        auditLogService.log("ROOM", id, "ROOM_DELETED", room.getRoomNumber(), null, "ADMIN");
    }

    @Transactional
    public RoomDTO updateRoomStatus(Long id, RoomStatus newStatus, String changedBy) {
        Room room = roomStatusService.changeRoomStatus(id, newStatus, changedBy);
        return new RoomDTO(room);
    }
}
