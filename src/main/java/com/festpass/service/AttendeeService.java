package com.festpass.service;

import com.festpass.dto.AttendeeRequest;
import com.festpass.dto.AttendeeResponse;
import com.festpass.entity.Attendee;
import com.festpass.exception.AttendeeNotFoundException;
import com.festpass.repository.AttendeeRepository;
import com.festpass.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final TicketRepository ticketRepository;

    public AttendeeService(AttendeeRepository attendeeRepository, TicketRepository ticketRepository) {
        this.attendeeRepository = attendeeRepository;
        this.ticketRepository = ticketRepository;
    }

    public AttendeeResponse createAttendee(AttendeeRequest request) {
        Attendee attendee = new Attendee();
        attendee.setName(request.getName().trim());
        attendee.setEmail(request.getEmail().trim().toLowerCase());
        attendee.setPhone(request.getPhone().trim());
        attendee.setCollegeName(request.getCollegeName() != null ? request.getCollegeName().trim() : "");
        attendee.setStudentId(request.getStudentId().trim().toUpperCase());

        Attendee saved = attendeeRepository.save(attendee);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AttendeeResponse> getAllAttendees() {
        return attendeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Attendee getAttendeeEntity(Long id) {
        return attendeeRepository.findById(id)
                .orElseThrow(() -> new AttendeeNotFoundException("Attendee not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public AttendeeResponse getAttendeeById(Long id) {
        Attendee attendee = getAttendeeEntity(id);
        return mapToResponse(attendee);
    }

    public AttendeeResponse mapToResponse(Attendee attendee) {
        long ticketCount = ticketRepository.findByAttendeeId(attendee.getId()).size();
        AttendeeResponse resp = new AttendeeResponse();
        resp.setId(attendee.getId());
        resp.setName(attendee.getName());
        resp.setEmail(attendee.getEmail());
        resp.setPhone(attendee.getPhone());
        resp.setCollegeName(attendee.getCollegeName());
        resp.setStudentId(attendee.getStudentId());
        resp.setCreatedAt(attendee.getCreatedAt());
        resp.setTotalTickets(ticketCount);
        return resp;
    }
}
