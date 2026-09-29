package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.CleaningTaskDTO;
import com.example.housekeeptrack.dto.HousekeeperDTO;
import com.example.housekeeptrack.service.HousekeeperService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/housekeepers")
@CrossOrigin(origins = "*")
public class HousekeeperController {

    private final HousekeeperService housekeeperService;

    public HousekeeperController(HousekeeperService housekeeperService) {
        this.housekeeperService = housekeeperService;
    }

    @GetMapping
    public ResponseEntity<List<HousekeeperDTO>> getAllHousekeepers() {
        return ResponseEntity.ok(housekeeperService.getAllHousekeepers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousekeeperDTO> getHousekeeperById(@PathVariable Long id) {
        return ResponseEntity.ok(housekeeperService.getHousekeeperById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<HousekeeperDTO>> getAvailableHousekeepers() {
        return ResponseEntity.ok(housekeeperService.getAvailableHousekeepers());
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<CleaningTaskDTO>> getHousekeeperTasks(@PathVariable Long id) {
        return ResponseEntity.ok(housekeeperService.getHousekeeperTasks(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HousekeeperDTO>> createHousekeeper(@Valid @RequestBody HousekeeperDTO dto) {
        HousekeeperDTO created = housekeeperService.createHousekeeper(dto);
        return new ResponseEntity<>(ApiResponse.success("Housekeeper created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HousekeeperDTO>> updateHousekeeper(@PathVariable Long id, @RequestBody HousekeeperDTO dto) {
        HousekeeperDTO updated = housekeeperService.updateHousekeeper(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Housekeeper updated successfully", updated));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<HousekeeperDTO>> updateHousekeeperWithoutPathId(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) HousekeeperDTO dto) {
        Long targetId = id;
        if (targetId == null && dto != null && dto.getId() != null) {
            targetId = dto.getId();
        }
        if (targetId == null) {
            throw new IllegalArgumentException("Housekeeper ID is required for update. Please include the ID in the URL (e.g., PUT /api/housekeepers/1 or ?id=1, or include 'id' in JSON body).");
        }
        HousekeeperDTO updated = housekeeperService.updateHousekeeper(targetId, dto != null ? dto : new HousekeeperDTO());
        return ResponseEntity.ok(ApiResponse.success("Housekeeper updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHousekeeper(@PathVariable Long id) {
        housekeeperService.deleteHousekeeper(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHousekeeperWithoutPathId(@RequestParam(required = false) Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Housekeeper ID is required for deletion. Please include the ID in the URL (e.g., DELETE /api/housekeepers/1 or DELETE /api/housekeepers?id=1).");
        }
        housekeeperService.deleteHousekeeper(id);
        return ResponseEntity.noContent().build();
    }
}
