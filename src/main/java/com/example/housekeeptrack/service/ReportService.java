package com.example.housekeeptrack.service;

import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReportService {

    private final CleaningTaskRepository cleaningTaskRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final InspectionRepository inspectionRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public ReportService(CleaningTaskRepository cleaningTaskRepository,
                         HousekeeperRepository housekeeperRepository,
                         InspectionRepository inspectionRepository,
                         RoomRepository roomRepository,
                         BookingRepository bookingRepository) {
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.inspectionRepository = inspectionRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getComprehensiveReport() {
        Map<String, Object> report = new HashMap<>();

        // 1. Turnaround time analysis
        List<CleaningTask> tasks = cleaningTaskRepository.findAll();
        List<Map<String, Object>> turnaroundList = new ArrayList<>();
        long totalMins = 0;
        long completedCount = 0;

        for (CleaningTask t : tasks) {
            if (t.getAssignedAt() != null && t.getCompletedAt() != null) {
                long mins = ChronoUnit.MINUTES.between(t.getAssignedAt(), t.getCompletedAt());
                if (mins >= 0) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("taskId", t.getId());
                    item.put("roomNumber", t.getRoom() != null ? t.getRoom().getRoomNumber() : "N/A");
                    item.put("housekeeper", t.getHousekeeper() != null ? t.getHousekeeper().getName() : "Unassigned");
                    item.put("assignedAt", t.getAssignedAt().toString());
                    item.put("completedAt", t.getCompletedAt().toString());
                    item.put("turnaroundMinutes", mins);
                    item.put("turnaroundHours", String.format("%.2f", (double) mins / 60.0));
                    turnaroundList.add(item);

                    totalMins += mins;
                    completedCount++;
                }
            }
        }
        double avgMins = completedCount > 0 ? (double) totalMins / completedCount : 0.0;
        report.put("turnaroundRecords", turnaroundList);
        report.put("averageTurnaroundMinutes", Math.round(avgMins * 10.0) / 10.0);
        report.put("averageTurnaroundHours", String.format("%.2f", avgMins / 60.0));

        // 2. Housekeeper workload
        List<Housekeeper> housekeepers = housekeeperRepository.findAll();
        List<Map<String, Object>> workloadList = new ArrayList<>();
        for (Housekeeper hk : housekeepers) {
            List<CleaningTask> hkTasks = cleaningTaskRepository.findByHousekeeperId(hk.getId());
            long active = hkTasks.stream().filter(t -> t.getStatus() != CleaningTaskStatus.COMPLETED).count();
            long completed = hkTasks.stream().filter(t -> t.getStatus() == CleaningTaskStatus.COMPLETED).count();

            Map<String, Object> item = new HashMap<>();
            item.put("id", hk.getId());
            item.put("name", hk.getName());
            item.put("status", hk.getStatus().name());
            item.put("activeTasks", active);
            item.put("completedTasks", completed);
            item.put("totalAssigned", hkTasks.size());
            workloadList.add(item);
        }
        report.put("housekeeperWorkload", workloadList);

        // 3. Inspection summary
        long passedInspections = inspectionRepository.countByInspectionStatus(InspectionStatus.PASSED);
        long failedInspections = inspectionRepository.countByInspectionStatus(InspectionStatus.FAILED);
        long totalInspections = passedInspections + failedInspections;
        double passRate = totalInspections > 0 ? ((double) passedInspections / totalInspections) * 100 : 100.0;

        Map<String, Object> inspSummary = new HashMap<>();
        inspSummary.put("total", totalInspections);
        inspSummary.put("passed", passedInspections);
        inspSummary.put("failed", failedInspections);
        inspSummary.put("passRatePercentage", Math.round(passRate * 10.0) / 10.0);
        report.put("inspectionSummary", inspSummary);

        // 4. Room status distribution
        Map<String, Long> statusCounts = new HashMap<>();
        for (RoomStatus rs : RoomStatus.values()) {
            statusCounts.put(rs.name(), roomRepository.countByStatus(rs));
        }
        report.put("roomStatusCounts", statusCounts);
        report.put("totalRooms", roomRepository.count());

        // 5. Booking counts
        report.put("totalBookings", bookingRepository.count());

        return report;
    }
}
