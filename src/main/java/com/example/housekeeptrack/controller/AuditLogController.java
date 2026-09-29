package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.entity.AuditLog;
import com.example.housekeeptrack.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getLogById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuditLog>> createAuditLog(@RequestBody AuditLog auditLog) {
        AuditLog created = auditLogService.createLog(auditLog);
        return new ResponseEntity<>(ApiResponse.success("Audit log created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> updateAuditLog(@PathVariable Long id, @RequestBody AuditLog auditLog) {
        AuditLog updated = auditLogService.updateLog(id, auditLog);
        return ResponseEntity.ok(ApiResponse.success("Audit log updated successfully", updated));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AuditLog>> updateAuditLogWithoutPathId(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) AuditLog auditLog) {
        Long targetId = id;
        if (targetId == null && auditLog != null && auditLog.getId() != null) {
            targetId = auditLog.getId();
        }
        if (targetId == null) {
            throw new IllegalArgumentException("Audit log ID is required for update. Please include the ID in the URL (e.g., PUT /api/audit-logs/1 or ?id=1, or include 'id' in JSON body).");
        }
        AuditLog updated = auditLogService.updateLog(targetId, auditLog != null ? auditLog : new AuditLog());
        return ResponseEntity.ok(ApiResponse.success("Audit log updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable Long id) {
        auditLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAuditLogWithoutPathId(@RequestParam(required = false) Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Audit log ID is required for deletion. Please include the ID in the URL (e.g., DELETE /api/audit-logs/1 or DELETE /api/audit-logs?id=1).");
        }
        auditLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
