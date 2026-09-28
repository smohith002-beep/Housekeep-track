package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.RoomDTO;
import com.example.housekeeptrack.dto.RoomStatusUpdateDTO;
import com.example.housekeeptrack.entity.RoomStatus;
import com.example.housekeeptrack.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomDTO>> getRoomsByStatus(@PathVariable RoomStatus status) {
        return ResponseEntity.ok(roomService.getRoomsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomDTO>> createRoom(@Valid @RequestBody RoomDTO dto) {
        RoomDTO created = roomService.createRoom(dto);
        return new ResponseEntity<>(ApiResponse.success("Room created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomDTO>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        RoomDTO updated = roomService.updateRoom(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomDTO>> updateRoomStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoomStatusUpdateDTO statusDto,
            Principal principal) {
        String username = principal != null ? principal.getName() : "USER";
        RoomDTO updated = roomService.updateRoomStatus(id, statusDto.getStatus(), username);
        return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", updated));
    }
}
