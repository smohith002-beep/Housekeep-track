package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.DashboardStatsDTO;
import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class DashboardService {

    private final RoomRepository roomRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final InspectionRepository inspectionRepository;

    public DashboardService(RoomRepository roomRepository,
                            HousekeeperRepository housekeeperRepository,
                            CleaningTaskRepository cleaningTaskRepository,
                            InspectionRepository inspectionRepository) {
        this.roomRepository = roomRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.inspectionRepository = inspectionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        long totalRooms = roomRepository.count();
        long ready = roomRepository.countByStatus(RoomStatus.READY);
        long dirty = roomRepository.countByStatus(RoomStatus.DIRTY);
        long cleaning = roomRepository.countByStatus(RoomStatus.CLEANING);
        long inspected = roomRepository.countByStatus(RoomStatus.INSPECTED);

        long availableHk = housekeeperRepository.countByStatus(HousekeeperStatus.AVAILABLE);
        long busyHk = housekeeperRepository.countByStatus(HousekeeperStatus.BUSY);

        long activeTasks = cleaningTaskRepository.countByStatusIn(Arrays.asList(
                CleaningTaskStatus.ASSIGNED,
                CleaningTaskStatus.IN_PROGRESS,
                CleaningTaskStatus.REOPENED
        ));
        long completedTasks = cleaningTaskRepository.countByStatus(CleaningTaskStatus.COMPLETED);
        long failedInspections = inspectionRepository.countByInspectionStatus(InspectionStatus.FAILED);

        // Calculate average turnaround time
        List<CleaningTask> allTasks = cleaningTaskRepository.findAll();
        long totalMinutes = 0;
        long completedCount = 0;
        for (CleaningTask t : allTasks) {
            if (t.getAssignedAt() != null && t.getCompletedAt() != null) {
                long mins = ChronoUnit.MINUTES.between(t.getAssignedAt(), t.getCompletedAt());
                if (mins >= 0) {
                    totalMinutes += mins;
                    completedCount++;
                }
            }
        }
        double avgMins = completedCount > 0 ? (double) totalMinutes / completedCount : 35.0;

        stats.setTotalRooms(totalRooms);
        stats.setReadyRooms(ready);
        stats.setDirtyRooms(dirty);
        stats.setCleaningRooms(cleaning);
        stats.setInspectedRooms(inspected);
        stats.setInspectionRooms(inspected); // Section 20 requirement
        stats.setAvailableHousekeepers(availableHk);
        stats.setBusyHousekeepers(busyHk);
        stats.setActiveTasks(activeTasks);
        stats.setCompletedTasks(completedTasks);
        stats.setFailedInspections(failedInspections);
        stats.setAvgTurnaroundMinutes(Math.round(avgMins * 10.0) / 10.0);

        Map<String, Long> statusDist = new LinkedHashMap<>();
        statusDist.put("READY", ready);
        statusDist.put("DIRTY", dirty);
        statusDist.put("CLEANING", cleaning);
        statusDist.put("INSPECTED", inspected);
        stats.setRoomStatusDistribution(statusDist);

        Map<String, Long> hkWorkload = new LinkedHashMap<>();
        List<Housekeeper> housekeepers = housekeeperRepository.findAll();
        for (Housekeeper hk : housekeepers) {
            long count = cleaningTaskRepository.findByHousekeeperId(hk.getId()).stream()
                    .filter(t -> t.getStatus() != CleaningTaskStatus.COMPLETED)
                    .count();
            hkWorkload.put(hk.getName(), count);
        }
        stats.setHousekeeperWorkload(hkWorkload);

        return stats;
    }
}
