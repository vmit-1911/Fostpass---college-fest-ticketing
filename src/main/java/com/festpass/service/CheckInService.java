package com.festpass.service;

import com.festpass.dto.CheckInRequest;
import com.festpass.dto.CheckInResponse;
import com.festpass.dto.DashboardStatsResponse.RecentCheckInItem;
import com.festpass.entity.Ticket;
import com.festpass.entity.TicketStatus;
import com.festpass.exception.InvalidQrCodeException;
import com.festpass.exception.TicketAlreadyUsedException;
import com.festpass.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckInService {

    private final TicketRepository ticketRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public CheckInService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public synchronized CheckInResponse checkIn(CheckInRequest request) {
        if (request.getQrCode() == null || request.getQrCode().trim().isEmpty()) {
            throw new InvalidQrCodeException("Invalid QR code. Token cannot be blank.");
        }

        String qrToken = request.getQrCode().trim();

        // 1. Search for ticket
        Ticket ticket = ticketRepository.findByQrCode(qrToken)
                .orElseThrow(() -> new InvalidQrCodeException("Invalid QR code. No valid ticket was found."));

        // 2. Validate ticket status - ONE-TIME CHECK-IN ENFORCEMENT
        if (ticket.getStatus() == TicketStatus.USED) {
            String checkInInfo = (ticket.getCheckedInAt() != null)
                    ? " at " + ticket.getCheckedInAt().format(timeFormatter)
                    : "";
            throw new TicketAlreadyUsedException("Ticket has already been used" + checkInInfo + ".");
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket is cancelled.");
        }

        // 3. Mark ACTIVE ticket as USED
        LocalDateTime now = LocalDateTime.now();
        ticket.setStatus(TicketStatus.USED);
        ticket.setCheckedInAt(now);
        Ticket saved = ticketRepository.save(ticket);

        return new CheckInResponse(
                true,
                "Check-in successful",
                saved.getTicketNumber(),
                saved.getAttendee().getName(),
                saved.getEvent().getEventName(),
                now.format(timeFormatter),
                now
        );
    }

    @Transactional(readOnly = true)
    public List<RecentCheckInItem> getRecentCheckIns() {
        return ticketRepository.findTop10ByStatusOrderByCheckedInAtDesc(TicketStatus.USED).stream()
                .map(t -> new RecentCheckInItem(
                        t.getTicketNumber(),
                        t.getAttendee().getName(),
                        t.getEvent().getEventName(),
                        (t.getCheckedInAt() != null) ? t.getCheckedInAt().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")) : "N/A",
                        t.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}
