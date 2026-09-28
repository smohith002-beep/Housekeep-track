package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.InspectionDTO;
import com.example.housekeeptrack.dto.InspectionDecisionDTO;
import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.HousekeeperRepository;
import com.example.housekeeptrack.repository.InspectionRepository;
import com.example.housekeeptrack.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final RoomRepository roomRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final RoomStatusService roomStatusService;
    private final AuditLogService auditLogService;

    public InspectionService(InspectionRepository inspectionRepository,
                             RoomRepository roomRepository,
                             CleaningTaskRepository cleaningTaskRepository,
                             HousekeeperRepository housekeeperRepository,
                             RoomStatusService roomStatusService,
                             AuditLogService auditLogService) {
        this.inspectionRepository = inspectionRepository;
        this.roomRepository = roomRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.roomStatusService = roomStatusService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<InspectionDTO> getAllInspections() {
        return inspectionRepository.findAllByOrderByInspectedAtDesc().stream()
                .map(InspectionDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InspectionDTO getInspectionById(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection with id " + id + " not found"));
        return new InspectionDTO(inspection);
    }

    @Transactional
    public InspectionDTO createInspection(InspectionDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + dto.getRoomId() + " not found"));

        Inspection inspection = new Inspection();
        inspection.setRoom(room);
        inspection.setSupervisorName(dto.getSupervisorName() != null ? dto.getSupervisorName() : "Supervisor");
        inspection.setInspectionStatus(dto.getInspectionStatus() != null ? dto.getInspectionStatus() : InspectionStatus.PASSED);
        inspection.setRemarks(dto.getRemarks() != null ? dto.getRemarks() : "Standard inspection");
        inspection.setInspectedAt(LocalDateTime.now());

        Inspection saved = inspectionRepository.save(inspection);
        auditLogService.log("INSPECTION", saved.getId(), "INSPECTION_CREATED", null, saved.getInspectionStatus().name(), saved.getSupervisorName());
        return new InspectionDTO(saved);
    }

    @Transactional
    public InspectionDTO updateInspection(Long id, InspectionDTO dto) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection with id " + id + " not found"));

        if (dto.getSupervisorName() != null) inspection.setSupervisorName(dto.getSupervisorName());
        if (dto.getInspectionStatus() != null) inspection.setInspectionStatus(dto.getInspectionStatus());
        if (dto.getRemarks() != null) inspection.setRemarks(dto.getRemarks());

        Inspection updated = inspectionRepository.save(inspection);
        return new InspectionDTO(updated);
    }

    @Transactional
    public void deleteInspection(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection with id " + id + " not found"));
        inspectionRepository.delete(inspection);
        auditLogService.log("INSPECTION", id, "INSPECTION_DELETED", String.valueOf(id), null, "ADMIN");
    }

    @Transactional
    public InspectionDTO passInspection(Long id, InspectionDecisionDTO decision) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection with id " + id + " not found"));

        return executePass(inspection, decision);
    }

    @Transactional
    public InspectionDTO passRoomInspection(Long roomId, InspectionDecisionDTO decision) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId + " not found"));

        Inspection inspection = new Inspection();
        inspection.setRoom(room);
        inspection.setSupervisorName(decision.getSupervisorName() != null ? decision.getSupervisorName() : "Supervisor");
        inspection.setRemarks(decision.getRemarks() != null ? decision.getRemarks() : "Inspected and approved for guest arrival.");
        inspection.setInspectionStatus(InspectionStatus.PASSED);
        inspection.setInspectedAt(LocalDateTime.now());

        return executePass(inspection, decision);
    }

    private InspectionDTO executePass(Inspection inspection, InspectionDecisionDTO decision) {
        Room room = inspection.getRoom();
        String supervisor = (decision != null && decision.getSupervisorName() != null && !decision.getSupervisorName().isBlank())
                ? decision.getSupervisorName() : "Supervisor";
        String remarks = (decision != null && decision.getRemarks() != null) ? decision.getRemarks() : "Room meets 5-star cleanliness standard.";

        inspection.setSupervisorName(supervisor);
        inspection.setRemarks(remarks);
        inspection.setInspectionStatus(InspectionStatus.PASSED);
        inspection.setInspectedAt(LocalDateTime.now());

        Inspection savedInspection = inspectionRepository.save(inspection);

        // 1. Change room status to READY
        roomStatusService.changeRoomStatus(room.getId(), RoomStatus.READY, supervisor);

        // 2. Find associated cleaning task
        Optional<CleaningTask> taskOpt = cleaningTaskRepository.findTopByRoomIdOrderByAssignedAtDesc(room.getId());
        if (taskOpt.isPresent()) {
            CleaningTask task = taskOpt.get();
            task.setStatus(CleaningTaskStatus.COMPLETED);
            if (task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            }
            cleaningTaskRepository.save(task);

            // 3. Free housekeeper (make AVAILABLE)
            Housekeeper housekeeper = task.getHousekeeper();
            if (housekeeper != null) {
                housekeeper.setStatus(HousekeeperStatus.AVAILABLE);
                housekeeper.setCurrentTaskId(null);
                housekeeperRepository.save(housekeeper);
            }
        }

        auditLogService.log("INSPECTION", savedInspection.getId(), "INSPECTION_PASSED", "INSPECTED", "READY", supervisor);
        return new InspectionDTO(savedInspection);
    }

    @Transactional
    public InspectionDTO failInspection(Long id, InspectionDecisionDTO decision) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection with id " + id + " not found"));

        return executeFail(inspection, decision);
    }

    @Transactional
    public InspectionDTO failRoomInspection(Long roomId, InspectionDecisionDTO decision) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId + " not found"));

        Inspection inspection = new Inspection();
        inspection.setRoom(room);
        inspection.setSupervisorName(decision.getSupervisorName() != null ? decision.getSupervisorName() : "Supervisor");
        inspection.setRemarks(decision.getRemarks() != null ? decision.getRemarks() : "Inspection failed. Cleaning incomplete.");
        inspection.setInspectionStatus(InspectionStatus.FAILED);
        inspection.setInspectedAt(LocalDateTime.now());

        return executeFail(inspection, decision);
    }

    private InspectionDTO executeFail(Inspection inspection, InspectionDecisionDTO decision) {
        Room room = inspection.getRoom();
        String supervisor = (decision != null && decision.getSupervisorName() != null && !decision.getSupervisorName().isBlank())
                ? decision.getSupervisorName() : "Supervisor";
        String remarks = (decision != null && decision.getRemarks() != null) ? decision.getRemarks() : "Cleaning incomplete. Needs re-cleaning.";

        inspection.setSupervisorName(supervisor);
        inspection.setRemarks(remarks);
        inspection.setInspectionStatus(InspectionStatus.FAILED);
        inspection.setInspectedAt(LocalDateTime.now());

        Inspection savedInspection = inspectionRepository.save(inspection);

        // 1. Move room back to CLEANING
        roomStatusService.changeRoomStatus(room.getId(), RoomStatus.CLEANING, supervisor);

        // 2. Reopen cleaning task
        Optional<CleaningTask> taskOpt = cleaningTaskRepository.findTopByRoomIdOrderByAssignedAtDesc(room.getId());
        if (taskOpt.isPresent()) {
            CleaningTask task = taskOpt.get();
            task.setStatus(CleaningTaskStatus.REOPENED);
            task.setNotes("Inspection failed: " + remarks);
            cleaningTaskRepository.save(task);

            if (task.getHousekeeper() != null) {
                task.getHousekeeper().setStatus(HousekeeperStatus.BUSY);
                task.getHousekeeper().setCurrentTaskId(task.getId());
                housekeeperRepository.save(task.getHousekeeper());
            }
        }

        auditLogService.log("INSPECTION", savedInspection.getId(), "INSPECTION_FAILED", "INSPECTED", "CLEANING", supervisor);
        return new InspectionDTO(savedInspection);
    }
}
