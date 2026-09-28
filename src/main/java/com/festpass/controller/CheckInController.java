package com.festpass.controller;

import com.festpass.dto.ApiResponse;
import com.festpass.dto.CheckInRequest;
import com.festpass.dto.CheckInResponse;
import com.festpass.dto.DashboardStatsResponse.RecentCheckInItem;
import com.festpass.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/check-in")
@CrossOrigin(origins = "*")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(@Valid @RequestBody CheckInRequest request) {
        CheckInResponse response = checkInService.checkIn(request);
        return ResponseEntity.ok(ApiResponse.ok("Check-in successful", response));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<RecentCheckInItem>>> getRecentCheckIns() {
        List<RecentCheckInItem> recent = checkInService.getRecentCheckIns();
        return ResponseEntity.ok(ApiResponse.ok("Recent check-ins fetched", recent));
    }
}
