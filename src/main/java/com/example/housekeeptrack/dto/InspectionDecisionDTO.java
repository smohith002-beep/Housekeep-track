package com.example.housekeeptrack.dto;

public class InspectionDecisionDTO {
    private String supervisorName;
    private String remarks;

    public InspectionDecisionDTO() {
    }

    public InspectionDecisionDTO(String supervisorName, String remarks) {
        this.supervisorName = supervisorName;
        this.remarks = remarks;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
