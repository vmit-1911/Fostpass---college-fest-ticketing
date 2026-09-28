package com.festpass.controller;

import com.festpass.dto.ApiResponse;
import com.festpass.dto.TicketIssueRequest;
import com.festpass.dto.TicketResponse;
import com.festpass.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> issueTicket(@Valid @RequestBody TicketIssueRequest request) {
        TicketResponse response = ticketService.issueTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Digital ticket issued successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.ok("Tickets fetched successfully", tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable Long id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.ok("Ticket fetched successfully", ticket));
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketByNumber(@PathVariable String ticketNumber) {
        TicketResponse ticket = ticketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(ApiResponse.ok("Ticket fetched successfully", ticket));
    }

    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketByQrCode(@PathVariable String qrCode) {
        TicketResponse ticket = ticketService.getTicketByQrCode(qrCode);
        return ResponseEntity.ok(ApiResponse.ok("Ticket fetched successfully", ticket));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsByEvent(@PathVariable Long eventId) {
        List<TicketResponse> tickets = ticketService.getTicketsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.ok("Event tickets fetched", tickets));
    }

    @GetMapping("/attendee/{attendeeId}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsByAttendee(@PathVariable Long attendeeId) {
        List<TicketResponse> tickets = ticketService.getTicketsByAttendee(attendeeId);
        return ResponseEntity.ok(ApiResponse.ok("Attendee tickets fetched", tickets));
    }

    @GetMapping(value = "/{id}/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTicketQrImage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {
        byte[] image = ticketService.getQrCodeImageBytes(id, width, height);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"festpass-qr-" + id + ".png\"");
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
