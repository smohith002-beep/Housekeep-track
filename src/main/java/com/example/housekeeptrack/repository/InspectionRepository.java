package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.Inspection;
import com.example.housekeeptrack.entity.InspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByRoomId(Long roomId);
    List<Inspection> findByInspectionStatus(InspectionStatus status);
    long countByInspectionStatus(InspectionStatus status);
    List<Inspection> findAllByOrderByInspectedAtDesc();
}
