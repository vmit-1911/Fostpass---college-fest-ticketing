package com.festpass.controller;

import com.festpass.dto.ApiResponse;
import com.festpass.dto.AttendanceResponse;
import com.festpass.dto.EventRequest;
import com.festpass.dto.EventResponse;
import com.festpass.service.FestEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class FestEventController {

    private final FestEventService festEventService;

    public FestEventController(FestEventService festEventService) {
        this.festEventService = festEventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse created = festEventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Fest Event created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents() {
        List<EventResponse> events = festEventService.getAllEvents();
        return ResponseEntity.ok(ApiResponse.ok("Events fetched successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = festEventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.ok("Event details fetched", event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        EventResponse updated = festEventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Fest Event updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        festEventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.ok("Fest Event deleted successfully"));
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendance(@PathVariable Long id) {
        AttendanceResponse attendance = festEventService.getEventAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok("Attendance fetched successfully", attendance));
    }
}
