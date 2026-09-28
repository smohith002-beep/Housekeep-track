package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.GuestDTO;
import com.example.housekeeptrack.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public ResponseEntity<List<GuestDTO>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getGuestById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GuestDTO>> createGuest(@Valid @RequestBody GuestDTO dto) {
        GuestDTO created = guestService.createGuest(dto);
        return new ResponseEntity<>(ApiResponse.success("Guest created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestDTO>> updateGuest(@PathVariable Long id, @Valid @RequestBody GuestDTO dto) {
        GuestDTO updated = guestService.updateGuest(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Guest updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}
