package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.CleaningTaskDTO;
import com.example.housekeeptrack.dto.HousekeeperDTO;
import com.example.housekeeptrack.entity.CleaningTask;
import com.example.housekeeptrack.entity.CleaningTaskStatus;
import com.example.housekeeptrack.entity.Housekeeper;
import com.example.housekeeptrack.entity.HousekeeperStatus;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.HousekeeperRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HousekeeperService {

    private final HousekeeperRepository housekeeperRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final AuditLogService auditLogService;

    public HousekeeperService(HousekeeperRepository housekeeperRepository,
                              CleaningTaskRepository cleaningTaskRepository,
                              AuditLogService auditLogService) {
        this.housekeeperRepository = housekeeperRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getAllHousekeepers() {
        return housekeeperRepository.findAll().stream()
                .map(this::enrichHousekeeperDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HousekeeperDTO getHousekeeperById(Long id) {
        Housekeeper housekeeper = getHousekeeperEntity(id);
        return enrichHousekeeperDTO(housekeeper);
    }

    @Transactional(readOnly = true)
    public Housekeeper getHousekeeperEntity(Long id) {
        return housekeeperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Housekeeper with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getAvailableHousekeepers() {
        return housekeeperRepository.findByStatus(HousekeeperStatus.AVAILABLE).stream()
                .map(this::enrichHousekeeperDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CleaningTaskDTO> getHousekeeperTasks(Long id) {
        getHousekeeperEntity(id);
        return cleaningTaskRepository.findByHousekeeperId(id).stream()
                .map(CleaningTaskDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public HousekeeperDTO createHousekeeper(HousekeeperDTO dto) {
        Housekeeper housekeeper = new Housekeeper();
        housekeeper.setName(dto.getName());
        housekeeper.setPhone(dto.getPhone());
        housekeeper.setEmail(dto.getEmail());
        housekeeper.setStatus(dto.getStatus() != null ? dto.getStatus() : HousekeeperStatus.AVAILABLE);

        Housekeeper saved = housekeeperRepository.save(housekeeper);
        auditLogService.log("HOUSEKEEPER", saved.getId(), "HOUSEKEEPER_CREATED", null, saved.getName(), "ADMIN");
        return enrichHousekeeperDTO(saved);
    }

    @Transactional
    public HousekeeperDTO updateHousekeeper(Long id, HousekeeperDTO dto) {
        Housekeeper housekeeper = getHousekeeperEntity(id);
        HousekeeperStatus oldStatus = housekeeper.getStatus();

        if (dto.getName() != null) housekeeper.setName(dto.getName());
        if (dto.getPhone() != null) housekeeper.setPhone(dto.getPhone());
        if (dto.getEmail() != null) housekeeper.setEmail(dto.getEmail());
        if (dto.getStatus() != null) {
            housekeeper.setStatus(dto.getStatus());
        }

        Housekeeper updated = housekeeperRepository.save(housekeeper);
        auditLogService.log("HOUSEKEEPER", updated.getId(), "HOUSEKEEPER_UPDATED", oldStatus.name(), updated.getStatus().name(), "ADMIN");
        return enrichHousekeeperDTO(updated);
    }

    @Transactional
    public void deleteHousekeeper(Long id) {
        Housekeeper housekeeper = getHousekeeperEntity(id);

        long taskCount = cleaningTaskRepository.findByHousekeeperId(id).size();
        if (taskCount > 0) {
            throw new DataIntegrityViolationException(
                    "Cannot delete Housekeeper " + housekeeper.getName() + " because they have " + taskCount + " assigned cleaning task(s).");
        }

        housekeeperRepository.delete(housekeeper);
        auditLogService.log("HOUSEKEEPER", id, "HOUSEKEEPER_DELETED", housekeeper.getName(), null, "ADMIN");
    }

    private HousekeeperDTO enrichHousekeeperDTO(Housekeeper housekeeper) {
        HousekeeperDTO dto = new HousekeeperDTO(housekeeper);

        if (housekeeper.getCurrentTaskId() != null) {
            Optional<CleaningTask> activeTask = cleaningTaskRepository.findById(housekeeper.getCurrentTaskId());
            if (activeTask.isPresent() && activeTask.get().getRoom() != null) {
                dto.setCurrentRoomNumber(activeTask.get().getRoom().getRoomNumber());
            }
        }

        List<CleaningTask> allTasks = cleaningTaskRepository.findByHousekeeperId(housekeeper.getId());
        long completed = allTasks.stream()
                .filter(t -> t.getStatus() == CleaningTaskStatus.COMPLETED)
                .count();
        dto.setCompletedTasksCount(completed);

        return dto;
    }
}
