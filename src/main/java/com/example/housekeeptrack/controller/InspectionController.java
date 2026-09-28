package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.InspectionDTO;
import com.example.housekeeptrack.dto.InspectionDecisionDTO;
import com.example.housekeeptrack.service.InspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "*")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping
    public ResponseEntity<List<InspectionDTO>> getAllInspections() {
        return ResponseEntity.ok(inspectionService.getAllInspections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionDTO> getInspectionById(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionService.getInspectionById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InspectionDTO>> createInspection(@RequestBody InspectionDTO dto) {
        InspectionDTO created = inspectionService.createInspection(dto);
        return new ResponseEntity<>(ApiResponse.success("Inspection created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionDTO>> updateInspection(@PathVariable Long id, @RequestBody InspectionDTO dto) {
        InspectionDTO updated = inspectionService.updateInspection(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Inspection updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInspection(@PathVariable Long id) {
        inspectionService.deleteInspection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pass")
    public ResponseEntity<ApiResponse<InspectionDTO>> passInspection(
            @PathVariable Long id,
            @RequestBody(required = false) InspectionDecisionDTO decision) {
        if (decision == null) decision = new InspectionDecisionDTO("Supervisor", "Inspection passed.");
        InspectionDTO passed = inspectionService.passInspection(id, decision);
        return ResponseEntity.ok(ApiResponse.success("Inspection passed successfully. Room is READY.", passed));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<InspectionDTO>> failInspection(
            @PathVariable Long id,
            @RequestBody(required = false) InspectionDecisionDTO decision) {
        if (decision == null) decision = new InspectionDecisionDTO("Supervisor", "Inspection failed. Re-cleaning required.");
        InspectionDTO failed = inspectionService.failInspection(id, decision);
        return ResponseEntity.ok(ApiResponse.success("Inspection failed. Room reverted to CLEANING.", failed));
    }

    @PostMapping("/rooms/{roomId}/pass")
    public ResponseEntity<ApiResponse<InspectionDTO>> passRoomInspection(
            @PathVariable Long roomId,
            @RequestBody(required = false) InspectionDecisionDTO decision) {
        if (decision == null) decision = new InspectionDecisionDTO("Supervisor", "Inspection passed.");
        InspectionDTO passed = inspectionService.passRoomInspection(roomId, decision);
        return ResponseEntity.ok(ApiResponse.success("Room inspection passed successfully. Room is READY.", passed));
    }

    @PostMapping("/rooms/{roomId}/fail")
    public ResponseEntity<ApiResponse<InspectionDTO>> failRoomInspection(
            @PathVariable Long roomId,
            @RequestBody(required = false) InspectionDecisionDTO decision) {
        if (decision == null) decision = new InspectionDecisionDTO("Supervisor", "Inspection failed. Re-cleaning required.");
        InspectionDTO failed = inspectionService.failRoomInspection(roomId, decision);
        return ResponseEntity.ok(ApiResponse.success("Room inspection failed. Room reverted to CLEANING.", failed));
    }
}
