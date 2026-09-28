package com.example.housekeeptrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotBlank(message = "Supervisor name is required")
    @Column(name = "supervisor_name", nullable = false, length = 100)
    private String supervisorName;

    @NotNull(message = "Inspection status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status", nullable = false, length = 30)
    private InspectionStatus inspectionStatus = InspectionStatus.PASSED;

    @Column(length = 500)
    private String remarks;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    public Inspection() {
    }

    public Inspection(Room room, String supervisorName, InspectionStatus inspectionStatus, String remarks) {
        this.room = room;
        this.supervisorName = supervisorName;
        this.inspectionStatus = inspectionStatus;
        this.remarks = remarks;
        this.inspectedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.inspectedAt == null) {
            this.inspectedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
