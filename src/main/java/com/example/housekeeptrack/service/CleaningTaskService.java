package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.CleaningTaskDTO;
import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.HousekeeperRepository;
import com.example.housekeeptrack.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CleaningTaskService {

    private final CleaningTaskRepository cleaningTaskRepository;
    private final RoomRepository roomRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final RoomStatusService roomStatusService;
    private final HousekeeperAssignmentService housekeeperAssignmentService;
    private final AuditLogService auditLogService;

    public CleaningTaskService(CleaningTaskRepository cleaningTaskRepository,
                               RoomRepository roomRepository,
                               HousekeeperRepository housekeeperRepository,
                               RoomStatusService roomStatusService,
                               HousekeeperAssignmentService housekeeperAssignmentService,
                               AuditLogService auditLogService) {
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.roomRepository = roomRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.roomStatusService = roomStatusService;
        this.housekeeperAssignmentService = housekeeperAssignmentService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<CleaningTaskDTO> getAllTasks() {
        return cleaningTaskRepository.findAllByOrderByAssignedAtDesc().stream()
                .map(CleaningTaskDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CleaningTaskDTO getTaskById(Long id) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));
        return new CleaningTaskDTO(task);
    }

    @Transactional(readOnly = true)
    public List<CleaningTaskDTO> getActiveTasks() {
        return cleaningTaskRepository.findByStatusIn(Arrays.asList(
                CleaningTaskStatus.ASSIGNED,
                CleaningTaskStatus.IN_PROGRESS,
                CleaningTaskStatus.REOPENED
        )).stream().map(CleaningTaskDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public CleaningTaskDTO createTask(CleaningTaskDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + dto.getRoomId() + " not found"));

        CleaningTask task = new CleaningTask();
        task.setRoom(room);
        task.setNotes(dto.getNotes() != null ? dto.getNotes() : "Standard cleaning");
        task.setAssignedAt(LocalDateTime.now());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : CleaningTaskStatus.ASSIGNED);

        if (dto.getHousekeeperId() != null) {
            Housekeeper housekeeper = housekeeperRepository.findById(dto.getHousekeeperId())
                    .orElseThrow(() -> new ResourceNotFoundException("Housekeeper with id " + dto.getHousekeeperId() + " not found"));
            if (housekeeper.getStatus() == HousekeeperStatus.BUSY && (task.getId() == null || !task.getId().equals(housekeeper.getCurrentTaskId()))) {
                throw new IllegalStateException("Housekeeper " + housekeeper.getName() + " is already working on an active task.");
            }
            task.setHousekeeper(housekeeper);
            housekeeper.setStatus(HousekeeperStatus.BUSY);
            CleaningTask savedTask = cleaningTaskRepository.save(task);
            housekeeper.setCurrentTaskId(savedTask.getId());
            housekeeperRepository.save(housekeeper);
            auditLogService.log("TASK", savedTask.getId(), "TASK_CREATED", null, "Assigned to " + housekeeper.getName(), "ADMIN");
            return new CleaningTaskDTO(savedTask);
        } else {
            CleaningTask savedTask = cleaningTaskRepository.save(task);
            housekeeperAssignmentService.assignAvailableHousekeeper(savedTask);
            auditLogService.log("TASK", savedTask.getId(), "TASK_CREATED", null, "Auto-assigned", "SYSTEM");
            return new CleaningTaskDTO(savedTask);
        }
    }

    @Transactional
    public CleaningTaskDTO updateTask(Long id, CleaningTaskDTO dto) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));

        if (dto.getNotes() != null) {
            task.setNotes(dto.getNotes());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        if (dto.getHousekeeperId() != null) {
            Housekeeper housekeeper = housekeeperRepository.findById(dto.getHousekeeperId())
                    .orElseThrow(() -> new ResourceNotFoundException("Housekeeper with id " + dto.getHousekeeperId() + " not found"));
            task.setHousekeeper(housekeeper);
            housekeeper.setStatus(HousekeeperStatus.BUSY);
            housekeeper.setCurrentTaskId(task.getId());
            housekeeperRepository.save(housekeeper);
        }

        CleaningTask updated = cleaningTaskRepository.save(task);
        return new CleaningTaskDTO(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));

        // If housekeeper was associated with this task, clear task reference
        Housekeeper hk = task.getHousekeeper();
        if (hk != null && id.equals(hk.getCurrentTaskId())) {
            hk.setCurrentTaskId(null);
            hk.setStatus(HousekeeperStatus.AVAILABLE);
            housekeeperRepository.save(hk);
        }

        cleaningTaskRepository.delete(task);
        auditLogService.log("TASK", id, "TASK_DELETED", String.valueOf(id), null, "ADMIN");
    }

    @Transactional
    public CleaningTaskDTO startTask(Long id) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));

        task.setStatus(CleaningTaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());

        Room room = task.getRoom();
        if (room != null && room.getStatus() == RoomStatus.DIRTY) {
            roomStatusService.changeRoomStatus(room.getId(), RoomStatus.CLEANING, "HOUSEKEEPER");
        }

        CleaningTask updated = cleaningTaskRepository.save(task);
        auditLogService.log("TASK", id, "TASK_STARTED", "ASSIGNED", "IN_PROGRESS",
                task.getHousekeeper() != null ? task.getHousekeeper().getName() : "HOUSEKEEPER");
        return new CleaningTaskDTO(updated);
    }

    @Transactional
    public CleaningTaskDTO completeTask(Long id) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));

        task.setStatus(CleaningTaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());

        Room room = task.getRoom();
        if (room != null && (room.getStatus() == RoomStatus.CLEANING || room.getStatus() == RoomStatus.DIRTY)) {
            roomStatusService.changeRoomStatus(room.getId(), RoomStatus.INSPECTED, "HOUSEKEEPER");
        }

        CleaningTask updated = cleaningTaskRepository.save(task);
        auditLogService.log("TASK", id, "TASK_COMPLETED", "IN_PROGRESS", "COMPLETED",
                task.getHousekeeper() != null ? task.getHousekeeper().getName() : "HOUSEKEEPER");
        return new CleaningTaskDTO(updated);
    }

    @Transactional
    public CleaningTaskDTO reopenTask(Long id, String notes) {
        CleaningTask task = cleaningTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task with id " + id + " not found"));

        task.setStatus(CleaningTaskStatus.REOPENED);
        if (notes != null && !notes.isBlank()) {
            task.setNotes(notes);
        }

        Room room = task.getRoom();
        if (room != null && room.getStatus() == RoomStatus.INSPECTED) {
            roomStatusService.changeRoomStatus(room.getId(), RoomStatus.CLEANING, "SUPERVISOR");
        }

        CleaningTask updated = cleaningTaskRepository.save(task);
        auditLogService.log("TASK", id, "TASK_REOPENED", "COMPLETED", "REOPENED", "SUPERVISOR");
        return new CleaningTaskDTO(updated);
    }
}
