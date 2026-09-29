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
    public ResponseEntity<ApiResponse<GuestDTO>> updateGuest(@PathVariable Long id, @RequestBody GuestDTO dto) {
        GuestDTO updated = guestService.updateGuest(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Guest updated successfully", updated));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<GuestDTO>> updateGuestWithoutPathId(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) GuestDTO dto) {
        Long targetId = id;
        if (targetId == null && dto != null && dto.getId() != null) {
            targetId = dto.getId();
        }
        if (targetId == null) {
            throw new IllegalArgumentException("Guest ID is required for update. Please include the ID in the URL (e.g., PUT /api/guests/1 or ?id=1, or include 'id' in JSON body).");
        }
        GuestDTO updated = guestService.updateGuest(targetId, dto != null ? dto : new GuestDTO());
        return ResponseEntity.ok(ApiResponse.success("Guest updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGuestWithoutPathId(@RequestParam(required = false) Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Guest ID is required for deletion. Please include the ID in the URL (e.g., DELETE /api/guests/1 or DELETE /api/guests?id=1).");
        }
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}
