package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.Inspection;
import com.example.housekeeptrack.entity.InspectionStatus;
import java.time.LocalDateTime;

public class InspectionDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String supervisorName;
    private InspectionStatus inspectionStatus;
    private String remarks;
    private LocalDateTime inspectedAt;

    public InspectionDTO() {
    }

    public InspectionDTO(Inspection inspection) {
        this.id = inspection.getId();
        if (inspection.getRoom() != null) {
            this.roomId = inspection.getRoom().getId();
            this.roomNumber = inspection.getRoom().getRoomNumber();
        }
        this.supervisorName = inspection.getSupervisorName();
        this.inspectionStatus = inspection.getInspectionStatus();
        this.remarks = inspection.getRemarks();
        this.inspectedAt = inspection.getInspectedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public InspectionStatus getInspectionStatus() {
        return inspectionStatus;
    }

    public void setInspectionStatus(InspectionStatus inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }

    public void setInspectedAt(LocalDateTime inspectedAt) {
        this.inspectedAt = inspectedAt;
    }
}
