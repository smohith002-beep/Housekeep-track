package com.example.housekeeptrack.service;

import com.example.housekeeptrack.entity.CleaningTask;
import com.example.housekeeptrack.entity.CleaningTaskStatus;
import com.example.housekeeptrack.entity.Housekeeper;
import com.example.housekeeptrack.entity.HousekeeperStatus;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.HousekeeperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HousekeeperAssignmentService {

    private final HousekeeperRepository housekeeperRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final AuditLogService auditLogService;

    public HousekeeperAssignmentService(HousekeeperRepository housekeeperRepository,
                                        CleaningTaskRepository cleaningTaskRepository,
                                        AuditLogService auditLogService) {
        this.housekeeperRepository = housekeeperRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public String assignAvailableHousekeeper(CleaningTask task) {
        List<Housekeeper> availableList = housekeeperRepository.findByStatus(HousekeeperStatus.AVAILABLE);

        if (!availableList.isEmpty()) {
            Housekeeper selectedHousekeeper = availableList.get(0);

            task.setHousekeeper(selectedHousekeeper);
            task.setStatus(CleaningTaskStatus.ASSIGNED);
            cleaningTaskRepository.save(task);

            selectedHousekeeper.setStatus(HousekeeperStatus.BUSY);
            selectedHousekeeper.setCurrentTaskId(task.getId());
            housekeeperRepository.save(selectedHousekeeper);

            auditLogService.log(
                    "TASK",
                    task.getId(),
                    "TASK_ASSIGNED",
                    "UNASSIGNED",
                    selectedHousekeeper.getName(),
                    "AUTO_ASSIGNER"
            );

            return "Housekeeper " + selectedHousekeeper.getName() + " assigned successfully.";
        } else {
            // Keep task as ASSIGNED with null housekeeper
            task.setStatus(CleaningTaskStatus.ASSIGNED);
            cleaningTaskRepository.save(task);

            auditLogService.log(
                    "TASK",
                    task.getId(),
                    "TASK_PENDING",
                    "NONE",
                    "NO_HOUSEKEEPER_AVAILABLE",
                    "AUTO_ASSIGNER"
            );

            return "No housekeeper currently available. Task added to pending queue.";
        }
    }
}
