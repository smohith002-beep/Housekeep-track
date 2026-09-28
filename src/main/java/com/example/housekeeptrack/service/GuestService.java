package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.GuestDTO;
import com.example.housekeeptrack.entity.Guest;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.repository.BookingRepository;
import com.example.housekeeptrack.repository.GuestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final AuditLogService auditLogService;

    public GuestService(GuestRepository guestRepository,
                        BookingRepository bookingRepository,
                        AuditLogService auditLogService) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<GuestDTO> getAllGuests() {
        return guestRepository.findAll().stream()
                .map(GuestDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuestDTO getGuestById(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest with id " + id + " not found"));
        return new GuestDTO(guest);
    }

    @Transactional(readOnly = true)
    public Guest getGuestEntity(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest with id " + id + " not found"));
    }

    @Transactional
    public GuestDTO createGuest(GuestDTO dto) {
        Guest guest = new Guest(dto.getName(), dto.getPhone(), dto.getEmail(), dto.getAddress());
        Guest saved = guestRepository.save(guest);
        auditLogService.log("GUEST", saved.getId(), "GUEST_CREATED", null, saved.getName(), "RECEPTIONIST");
        return new GuestDTO(saved);
    }

    @Transactional
    public GuestDTO updateGuest(Long id, GuestDTO dto) {
        Guest guest = getGuestEntity(id);

        if (dto.getName() != null) guest.setName(dto.getName());
        if (dto.getPhone() != null) guest.setPhone(dto.getPhone());
        if (dto.getEmail() != null) guest.setEmail(dto.getEmail());
        if (dto.getAddress() != null) guest.setAddress(dto.getAddress());

        Guest updated = guestRepository.save(guest);
        auditLogService.log("GUEST", updated.getId(), "GUEST_UPDATED", null, updated.getName(), "RECEPTIONIST");
        return new GuestDTO(updated);
    }

    @Transactional
    public void deleteGuest(Long id) {
        Guest guest = getGuestEntity(id);

        if (guest.getBookings() != null && !guest.getBookings().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Cannot delete Guest " + guest.getName() + " because they have " + guest.getBookings().size() + " reservation(s).");
        }

        guestRepository.delete(guest);
        auditLogService.log("GUEST", id, "GUEST_DELETED", guest.getName(), null, "ADMIN");
    }
}
