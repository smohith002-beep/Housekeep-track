package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.BookingRequestDTO;
import com.example.housekeeptrack.dto.BookingResponseDTO;
import com.example.housekeeptrack.dto.CheckoutResponseDTO;
import com.example.housekeeptrack.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        BookingResponseDTO created = bookingService.createBooking(request);
        return new ResponseEntity<>(ApiResponse.success("Booking created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(@PathVariable Long id, @RequestBody BookingRequestDTO request) {
        BookingResponseDTO updated = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", updated));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingWithoutPathId(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) BookingRequestDTO request) {
        if (id == null) {
            throw new IllegalArgumentException("Booking ID is required for update. Please include the ID in the URL (e.g., PUT /api/bookings/1 or ?id=1).");
        }
        BookingResponseDTO updated = bookingService.updateBooking(id, request != null ? request : new BookingRequestDTO());
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBookingWithoutPathId(@RequestParam(required = false) Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Booking ID is required for deletion. Please include the ID in the URL (e.g., DELETE /api/bookings/1 or DELETE /api/bookings?id=1).");
        }
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@PathVariable Long id) {
        CheckoutResponseDTO response = bookingService.checkout(id);
        return ResponseEntity.ok(response);
    }
}
