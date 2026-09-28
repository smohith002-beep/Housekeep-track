package com.example.housekeeptrack.repository;

import com.example.housekeeptrack.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByPhone(String phone);
}
