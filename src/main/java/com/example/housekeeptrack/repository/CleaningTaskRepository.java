package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.CleaningTask;
import com.example.housekeeptrack.entity.CleaningTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CleaningTaskRepository extends JpaRepository<CleaningTask, Long> {
    List<CleaningTask> findByStatus(CleaningTaskStatus status);
    List<CleaningTask> findByHousekeeperId(Long housekeeperId);
    List<CleaningTask> findByRoomId(Long roomId);
    Optional<CleaningTask> findTopByRoomIdOrderByAssignedAtDesc(Long roomId);
    List<CleaningTask> findByStatusIn(List<CleaningTaskStatus> statuses);
    long countByStatus(CleaningTaskStatus status);
    long countByStatusIn(List<CleaningTaskStatus> statuses);
    List<CleaningTask> findAllByOrderByAssignedAtDesc();
}
