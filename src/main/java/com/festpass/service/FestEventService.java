package com.festpass.service;

import com.festpass.dto.AttendanceResponse;
import com.festpass.dto.EventRequest;
import com.festpass.dto.EventResponse;
import com.festpass.entity.FestEvent;
import com.festpass.entity.TicketStatus;
import com.festpass.exception.EventNotFoundException;
import com.festpass.repository.FestEventRepository;
import com.festpass.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FestEventService {

    private final FestEventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public FestEventService(FestEventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    public EventResponse createEvent(EventRequest request) {
        FestEvent event = new FestEvent();
        event.setEventName(request.getEventName().trim());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue().trim());
        event.setCapacity(request.getCapacity());
        event.setTicketPrice(request.getTicketPrice());
        event.setStatus(request.getStatus());

        FestEvent saved = eventRepository.save(event);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FestEvent getEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Fest Event not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        FestEvent event = getEventEntity(id);
        return mapToResponse(event);
    }

    public EventResponse updateEvent(Long id, EventRequest request) {
        FestEvent event = getEventEntity(id);
        event.setEventName(request.getEventName().trim());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue().trim());
        event.setCapacity(request.getCapacity());
        event.setTicketPrice(request.getTicketPrice());
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }

        FestEvent updated = eventRepository.save(event);
        return mapToResponse(updated);
    }

    public void deleteEvent(Long id) {
        FestEvent event = getEventEntity(id);
        long ticketCount = ticketRepository.countByEventId(id);
        if (ticketCount > 0) {
            throw new IllegalStateException("Cannot delete event '" + event.getEventName() + "' because it has " + ticketCount + " issued ticket(s).");
        }
        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getEventAttendance(Long id) {
        FestEvent event = getEventEntity(id);
        long issued = ticketRepository.countByEventIdAndStatusNot(id, TicketStatus.CANCELLED);
        long checkedIn = ticketRepository.countByEventIdAndStatus(id, TicketStatus.USED);
        long remaining = Math.max(0, event.getCapacity() - issued);

        double percentage = 0.0;
        if (event.getCapacity() > 0) {
            percentage = Math.round(((double) checkedIn / event.getCapacity()) * 1000.0) / 10.0;
        }

        return new AttendanceResponse(
                event.getId(),
                event.getEventName(),
                event.getCapacity(),
                issued,
                checkedIn,
                remaining,
                percentage
        );
    }

    public EventResponse mapToResponse(FestEvent event) {
        long issued = ticketRepository.countByEventIdAndStatusNot(event.getId(), TicketStatus.CANCELLED);
        long checkedIn = ticketRepository.countByEventIdAndStatus(event.getId(), TicketStatus.USED);
        long remaining = Math.max(0, event.getCapacity() - issued);

        double percentage = 0.0;
        if (event.getCapacity() > 0) {
            percentage = Math.round(((double) checkedIn / event.getCapacity()) * 1000.0) / 10.0;
        }

        EventResponse resp = new EventResponse();
        resp.setId(event.getId());
        resp.setEventName(event.getEventName());
        resp.setDescription(event.getDescription());
        resp.setEventDate(event.getEventDate());
        resp.setVenue(event.getVenue());
        resp.setCapacity(event.getCapacity());
        resp.setTicketPrice(event.getTicketPrice());
        resp.setStatus(event.getStatus());
        resp.setCreatedAt(event.getCreatedAt());
        resp.setTicketsIssued(issued);
        resp.setCheckedIn(checkedIn);
        resp.setRemainingCapacity(remaining);
        resp.setAttendancePercentage(percentage);
        return resp;
    }
}
