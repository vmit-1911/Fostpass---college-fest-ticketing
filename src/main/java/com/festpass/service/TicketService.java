package com.festpass.service;

import com.festpass.dto.TicketIssueRequest;
import com.festpass.dto.TicketResponse;
import com.festpass.entity.Attendee;
import com.festpass.entity.EventStatus;
import com.festpass.entity.FestEvent;
import com.festpass.entity.Ticket;
import com.festpass.entity.TicketStatus;
import com.festpass.exception.EventCapacityExceededException;
import com.festpass.exception.TicketNotFoundException;
import com.festpass.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final FestEventService festEventService;
    private final AttendeeService attendeeService;
    private final QrCodeService qrCodeService;

    public TicketService(TicketRepository ticketRepository,
                         FestEventService festEventService,
                         AttendeeService attendeeService,
                         QrCodeService qrCodeService) {
        this.ticketRepository = ticketRepository;
        this.festEventService = festEventService;
        this.attendeeService = attendeeService;
        this.qrCodeService = qrCodeService;
    }

    public synchronized TicketResponse issueTicket(TicketIssueRequest request) {
        FestEvent event = festEventService.getEventEntity(request.getEventId());
        Attendee attendee = attendeeService.getAttendeeEntity(request.getAttendeeId());

        // Rule: Event status validation
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Cannot issue ticket. Event '" + event.getEventName() + "' has been cancelled.");
        }
        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Cannot issue ticket. Event '" + event.getEventName() + "' has already completed.");
        }

        // Rule: CAPACITY ENFORCEMENT
        long currentTicketsIssued = ticketRepository.countByEventIdAndStatusNot(event.getId(), TicketStatus.CANCELLED);
        if (currentTicketsIssued >= event.getCapacity()) {
            throw new EventCapacityExceededException("Ticket sales closed. Event capacity has been reached.");
        }

        // Generate unique ticket number: FP-YYYY-XXXXXX
        int year = LocalDate.now().getYear();
        long totalTickets = ticketRepository.count() + 1;
        String ticketNumber = String.format("FP-%d-%06d", year, totalTickets);

        // Ensure ticket number uniqueness
        while (ticketRepository.findByTicketNumber(ticketNumber).isPresent()) {
            totalTickets++;
            ticketNumber = String.format("FP-%d-%06d", year, totalTickets);
        }

        // Generate unique secure random QR token
        String qrToken = "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        while (ticketRepository.findByQrCode(qrToken).isPresent()) {
            qrToken = "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        }

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setQrCode(qrToken);
        ticket.setEvent(event);
        ticket.setAttendee(attendee);
        ticket.setTicketPrice(event.getTicketPrice());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setIssuedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAllByOrderByIssuedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Ticket getTicketEntity(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = getTicketEntity(id);
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber.trim())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with Ticket Number: " + ticketNumber));
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketByQrCode(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode.trim())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with QR token: " + qrCode));
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByAttendee(Long attendeeId) {
        return ticketRepository.findByAttendeeId(attendeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public byte[] getQrCodeImageBytes(Long ticketId, int width, int height) {
        Ticket ticket = getTicketEntity(ticketId);
        return qrCodeService.generateQrCodeImage(ticket.getQrCode(), width, height);
    }

    public TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse resp = new TicketResponse();
        resp.setId(ticket.getId());
        resp.setTicketNumber(ticket.getTicketNumber());
        resp.setQrCode(ticket.getQrCode());
        resp.setEventId(ticket.getEvent().getId());
        resp.setEventName(ticket.getEvent().getEventName());
        resp.setEventDate(ticket.getEvent().getEventDate());
        resp.setVenue(ticket.getEvent().getVenue());
        resp.setAttendeeId(ticket.getAttendee().getId());
        resp.setAttendeeName(ticket.getAttendee().getName());
        resp.setAttendeeEmail(ticket.getAttendee().getEmail());
        resp.setCollegeName(ticket.getAttendee().getCollegeName());
        resp.setStudentId(ticket.getAttendee().getStudentId());
        resp.setTicketPrice(ticket.getTicketPrice());
        resp.setStatus(ticket.getStatus());
        resp.setIssuedAt(ticket.getIssuedAt());
        resp.setCheckedInAt(ticket.getCheckedInAt());

        // Generate base64 QR code image for inline rendering
        try {
            resp.setQrCodeBase64(qrCodeService.generateQrCodeBase64(ticket.getQrCode(), 260, 260));
        } catch (Exception ignored) {
        }

        return resp;
    }
}
