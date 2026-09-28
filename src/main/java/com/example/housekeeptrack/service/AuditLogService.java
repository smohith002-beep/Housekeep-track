package com.example.housekeeptrack.service;

import com.example.housekeeptrack.entity.AuditLog;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog log(String entityType, Long entityId, String action, String oldValue, String newValue, String changedBy) {
        AuditLog log = new AuditLog(
                entityType,
                entityId,
                action,
                oldValue,
                newValue,
                changedBy != null ? changedBy : "SYSTEM"
        );
        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop50ByOrderByChangedAtDesc();
    }

    @Transactional(readOnly = true)
    public AuditLog getLogById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log with id " + id + " not found"));
    }

    @Transactional
    public AuditLog createLog(AuditLog auditLog) {
        if (auditLog.getChangedAt() == null) {
            auditLog.setChangedAt(LocalDateTime.now());
        }
        return auditLogRepository.save(auditLog);
    }

    @Transactional
    public AuditLog updateLog(Long id, AuditLog updated) {
        AuditLog existing = getLogById(id);
        if (updated.getEntityType() != null) existing.setEntityType(updated.getEntityType());
        if (updated.getEntityId() != null) existing.setEntityId(updated.getEntityId());
        if (updated.getAction() != null) existing.setAction(updated.getAction());
        if (updated.getOldValue() != null) existing.setOldValue(updated.getOldValue());
        if (updated.getNewValue() != null) existing.setNewValue(updated.getNewValue());
        if (updated.getChangedBy() != null) existing.setChangedBy(updated.getChangedBy());
        return auditLogRepository.save(existing);
    }

    @Transactional
    public void deleteLog(Long id) {
        AuditLog existing = getLogById(id);
        auditLogRepository.delete(existing);
    }
}
