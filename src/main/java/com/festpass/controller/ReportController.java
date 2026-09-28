package com.festpass.controller;

import com.festpass.dto.ApiResponse;
import com.festpass.dto.DashboardStatsResponse;
import com.festpass.dto.ReportSummaryResponse;
import com.festpass.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<ReportSummaryResponse>> getReports() {
        ReportSummaryResponse reports = reportService.getReports();
        return ResponseEntity.ok(ApiResponse.ok("Reports fetched successfully", reports));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = reportService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics fetched successfully", stats));
    }
}
