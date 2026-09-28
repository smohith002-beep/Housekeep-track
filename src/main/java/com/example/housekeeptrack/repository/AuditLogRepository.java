package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByChangedAtDesc();
    List<AuditLog> findTop50ByOrderByChangedAtDesc();
}
