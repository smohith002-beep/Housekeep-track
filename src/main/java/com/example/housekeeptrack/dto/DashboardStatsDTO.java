package com.example.housekeeptrack.dto;

import java.util.Map;

public class DashboardStatsDTO {
    private long totalRooms;
    private long readyRooms;
    private long dirtyRooms;
    private long cleaningRooms;
    private long inspectionRooms; // Section 20 field name
    private long inspectedRooms;
    private long availableHousekeepers;
    private long busyHousekeepers;
    private long activeTasks;
    private long completedTasks;
    private long failedInspections;
    private double avgTurnaroundMinutes;
    private Map<String, Long> roomStatusDistribution;
    private Map<String, Long> housekeeperWorkload;

    public DashboardStatsDTO() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getReadyRooms() {
        return readyRooms;
    }

    public void setReadyRooms(long readyRooms) {
        this.readyRooms = readyRooms;
    }

    public long getDirtyRooms() {
        return dirtyRooms;
    }

    public void setDirtyRooms(long dirtyRooms) {
        this.dirtyRooms = dirtyRooms;
    }

    public long getCleaningRooms() {
        return cleaningRooms;
    }

    public void setCleaningRooms(long cleaningRooms) {
        this.cleaningRooms = cleaningRooms;
    }

    public long getInspectionRooms() {
        return inspectionRooms;
    }

    public void setInspectionRooms(long inspectionRooms) {
        this.inspectionRooms = inspectionRooms;
        this.inspectedRooms = inspectionRooms;
    }

    public long getInspectedRooms() {
        return inspectedRooms;
    }

    public void setInspectedRooms(long inspectedRooms) {
        this.inspectedRooms = inspectedRooms;
        this.inspectionRooms = inspectedRooms;
    }

    public long getAvailableHousekeepers() {
        return availableHousekeepers;
    }

    public void setAvailableHousekeepers(long availableHousekeepers) {
        this.availableHousekeepers = availableHousekeepers;
    }

    public long getBusyHousekeepers() {
        return busyHousekeepers;
    }

    public void setBusyHousekeepers(long busyHousekeepers) {
        this.busyHousekeepers = busyHousekeepers;
    }

    public long getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(long activeTasks) {
        this.activeTasks = activeTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getFailedInspections() {
        return failedInspections;
    }

    public void setFailedInspections(long failedInspections) {
        this.failedInspections = failedInspections;
    }

    public double getAvgTurnaroundMinutes() {
        return avgTurnaroundMinutes;
    }

    public void setAvgTurnaroundMinutes(double avgTurnaroundMinutes) {
        this.avgTurnaroundMinutes = avgTurnaroundMinutes;
    }

    public Map<String, Long> getRoomStatusDistribution() {
        return roomStatusDistribution;
    }

    public void setRoomStatusDistribution(Map<String, Long> roomStatusDistribution) {
        this.roomStatusDistribution = roomStatusDistribution;
    }

    public Map<String, Long> getHousekeeperWorkload() {
        return housekeeperWorkload;
    }

    public void setHousekeeperWorkload(Map<String, Long> housekeeperWorkload) {
        this.housekeeperWorkload = housekeeperWorkload;
    }
}
