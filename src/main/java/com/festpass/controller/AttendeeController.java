package com.festpass.controller;

import com.festpass.dto.ApiResponse;
import com.festpass.dto.AttendeeRequest;
import com.festpass.dto.AttendeeResponse;
import com.festpass.service.AttendeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendees")
@CrossOrigin(origins = "*")
public class AttendeeController {

    private final AttendeeService attendeeService;

    public AttendeeController(AttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendeeResponse>> registerAttendee(@Valid @RequestBody AttendeeRequest request) {
        AttendeeResponse created = attendeeService.createAttendee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Attendee registered successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendeeResponse>>> getAllAttendees() {
        List<AttendeeResponse> attendees = attendeeService.getAllAttendees();
        return ResponseEntity.ok(ApiResponse.ok("Attendees fetched successfully", attendees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendeeResponse>> getAttendeeById(@PathVariable Long id) {
        AttendeeResponse attendee = attendeeService.getAttendeeById(id);
        return ResponseEntity.ok(ApiResponse.ok("Attendee details fetched", attendee));
    }
}
