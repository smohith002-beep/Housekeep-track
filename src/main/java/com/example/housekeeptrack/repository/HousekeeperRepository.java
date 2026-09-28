package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.Housekeeper;
import com.example.housekeeptrack.entity.HousekeeperStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeeperRepository extends JpaRepository<Housekeeper, Long> {
    List<Housekeeper> findByStatus(HousekeeperStatus status);
    long countByStatus(HousekeeperStatus status);
}
