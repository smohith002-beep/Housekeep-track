package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.dto.ApiResponse;
import com.example.housekeeptrack.dto.CleaningTaskDTO;
import com.example.housekeeptrack.service.CleaningTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cleaning-tasks")
@CrossOrigin(origins = "*")
public class CleaningTaskController {

    private final CleaningTaskService cleaningTaskService;

    public CleaningTaskController(CleaningTaskService cleaningTaskService) {
        this.cleaningTaskService = cleaningTaskService;
    }

    @GetMapping
    public ResponseEntity<List<CleaningTaskDTO>> getAllTasks() {
        return ResponseEntity.ok(cleaningTaskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CleaningTaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(cleaningTaskService.getTaskById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CleaningTaskDTO>> getActiveTasks() {
        return ResponseEntity.ok(cleaningTaskService.getActiveTasks());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CleaningTaskDTO>> createTask(@RequestBody CleaningTaskDTO dto) {
        CleaningTaskDTO created = cleaningTaskService.createTask(dto);
        return new ResponseEntity<>(ApiResponse.success("Cleaning task created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CleaningTaskDTO>> updateTask(@PathVariable Long id, @RequestBody CleaningTaskDTO dto) {
        CleaningTaskDTO updated = cleaningTaskService.updateTask(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Cleaning task updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        cleaningTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<CleaningTaskDTO>> startTask(@PathVariable Long id) {
        CleaningTaskDTO started = cleaningTaskService.startTask(id);
        return ResponseEntity.ok(ApiResponse.success("Cleaning task started successfully", started));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<CleaningTaskDTO>> completeTask(@PathVariable Long id) {
        CleaningTaskDTO completed = cleaningTaskService.completeTask(id);
        return ResponseEntity.ok(ApiResponse.success("Cleaning task completed successfully", completed));
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<CleaningTaskDTO>> reopenTask(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        CleaningTaskDTO reopened = cleaningTaskService.reopenTask(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Cleaning task reopened successfully", reopened));
    }
}
