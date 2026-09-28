package com.example.housekeeptrack.controller;

import com.example.housekeeptrack.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveReport() {
        return ResponseEntity.ok(reportService.getComprehensiveReport());
    }
}
