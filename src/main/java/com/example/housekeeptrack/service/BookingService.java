package com.example.housekeeptrack.service;

import com.example.housekeeptrack.dto.BookingRequestDTO;
import com.example.housekeeptrack.dto.BookingResponseDTO;
import com.example.housekeeptrack.dto.CheckoutResponseDTO;
import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.exception.ResourceNotFoundException;
import com.example.housekeeptrack.exception.RoomNotAvailableException;
import com.example.housekeeptrack.repository.BookingRepository;
import com.example.housekeeptrack.repository.CleaningTaskRepository;
import com.example.housekeeptrack.repository.GuestRepository;
import com.example.housekeeptrack.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final RoomStatusService roomStatusService;
    private final HousekeeperAssignmentService housekeeperAssignmentService;
    private final AuditLogService auditLogService;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          GuestRepository guestRepository,
                          CleaningTaskRepository cleaningTaskRepository,
                          RoomStatusService roomStatusService,
                          HousekeeperAssignmentService housekeeperAssignmentService,
                          AuditLogService auditLogService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.roomStatusService = roomStatusService;
        this.housekeeperAssignmentService = housekeeperAssignmentService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));
        return new BookingResponseDTO(booking);
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Booking request body is required");
        }
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }
        if (request.getCheckInDate() == null) {
            throw new IllegalArgumentException("Check-in date is required");
        }
        if (request.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-out date is required");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + request.getRoomId() + " not found"));

        // Section 9: Room must be READY. If DIRTY, CLEANING, INSPECTED, throw RoomNotAvailableException (409)
        if (room.getStatus() != RoomStatus.READY) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not ready for booking");
        }

        // Find or create Guest
        Guest guest = null;
        if (request.getGuestId() != null) {
            guest = guestRepository.findById(request.getGuestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Guest with id " + request.getGuestId() + " not found"));
        } else {
            if (request.getGuestEmail() != null && !request.getGuestEmail().isBlank()) {
                guest = guestRepository.findByEmail(request.getGuestEmail()).orElse(null);
            }
            if (guest == null && request.getGuestPhone() != null && !request.getGuestPhone().isBlank()) {
                guest = guestRepository.findByPhone(request.getGuestPhone()).orElse(null);
            }
            if (guest == null) {
                String gName = (request.getGuestName() != null && !request.getGuestName().isBlank())
                        ? request.getGuestName() : "Valued Guest";
                String gPhone = (request.getGuestPhone() != null && !request.getGuestPhone().isBlank())
                        ? request.getGuestPhone() : "N/A";
                guest = new Guest(gName, gPhone, request.getGuestEmail(), request.getGuestAddress());
                guest = guestRepository.save(guest);
            }
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) nights = 1;
        double totalAmount = nights * (room.getPricePerNight() != null ? room.getPricePerNight() : 18000.0);

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setBookingStatus(request.getBookingStatus() != null ? request.getBookingStatus() : BookingStatus.RESERVED);
        booking.setTotalAmount(totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        auditLogService.log("BOOKING", savedBooking.getId(), "BOOKING_CREATED", null,
                "Room " + room.getRoomNumber() + " booked for " + guest.getName(), "RECEPTIONIST");

        return new BookingResponseDTO(savedBooking);
    }

    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        if (request.getRoomId() != null && !request.getRoomId().equals(booking.getRoom().getId())) {
            Room newRoom = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room with id " + request.getRoomId() + " not found"));
            if (newRoom.getStatus() != RoomStatus.READY) {
                throw new RoomNotAvailableException("Room " + newRoom.getRoomNumber() + " is not ready for booking");
            }
            booking.setRoom(newRoom);
        }

        if (request.getCheckInDate() != null) booking.setCheckInDate(request.getCheckInDate());
        if (request.getCheckOutDate() != null) booking.setCheckOutDate(request.getCheckOutDate());
        if (request.getBookingStatus() != null) booking.setBookingStatus(request.getBookingStatus());

        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null && booking.getRoom() != null) {
            long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            if (nights <= 0) nights = 1;
            booking.setTotalAmount(nights * booking.getRoom().getPricePerNight());
        }

        Booking updated = bookingRepository.save(booking);
        auditLogService.log("BOOKING", updated.getId(), "BOOKING_UPDATED", null, "Updated reservation details", "RECEPTIONIST");
        return new BookingResponseDTO(updated);
    }

    @Transactional
    public CheckoutResponseDTO checkout(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getBookingStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Booking ID " + bookingId + " has already checked out.");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_OUT);
        bookingRepository.save(booking);

        Room room = booking.getRoom();
        if (room == null) {
            throw new ResourceNotFoundException("Associated room not found for booking ID: " + bookingId);
        }

        // Room becomes DIRTY
        roomStatusService.changeRoomStatus(room.getId(), RoomStatus.DIRTY, true, "RECEPTIONIST");

        // Automatically create CleaningTask
        CleaningTask task = new CleaningTask();
        task.setRoom(room);
        task.setStatus(CleaningTaskStatus.ASSIGNED);
        task.setAssignedAt(LocalDateTime.now());
        task.setNotes("Checkout cleaning for Room " + room.getRoomNumber() + " (Guest: " + booking.getGuest().getName() + ")");
        CleaningTask savedTask = cleaningTaskRepository.save(task);

        // Find AVAILABLE housekeeper and assign
        String assignmentMessage = housekeeperAssignmentService.assignAvailableHousekeeper(savedTask);

        auditLogService.log("BOOKING", booking.getId(), "BOOKING_CHECKED_OUT", "CHECKED_IN", "CHECKED_OUT", "RECEPTIONIST");

        String assignedHousekeeperName = savedTask.getHousekeeper() != null
                ? savedTask.getHousekeeper().getName()
                : "Pending Assignment";

        return new CheckoutResponseDTO(
                "Checkout completed and cleaning task assigned (" + assignmentMessage + ")",
                room.getRoomNumber(),
                room.getStatus().name(),
                savedTask.getId(),
                assignedHousekeeperName,
                savedTask.getStatus().name()
        );
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));
        bookingRepository.delete(booking);
        auditLogService.log("BOOKING", id, "BOOKING_DELETED", String.valueOf(booking.getId()), null, "ADMIN");
    }
}
