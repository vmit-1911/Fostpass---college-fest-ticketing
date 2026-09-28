package com.festpass.service;

import com.festpass.dto.DashboardStatsResponse;
import com.festpass.dto.EventResponse;
import com.festpass.dto.ReportSummaryResponse;
import com.festpass.dto.ReportSummaryResponse.EventReportItem;
import com.festpass.entity.EventStatus;
import com.festpass.entity.FestEvent;
import com.festpass.entity.Ticket;
import com.festpass.entity.TicketStatus;
import com.festpass.repository.FestEventRepository;
import com.festpass.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final FestEventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final FestEventService festEventService;
    private final CheckInService checkInService;

    public ReportService(FestEventRepository eventRepository,
                         TicketRepository ticketRepository,
                         FestEventService festEventService,
                         CheckInService checkInService) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.festEventService = festEventService;
        this.checkInService = checkInService;
    }

    public DashboardStatsResponse getDashboardStats() {
        List<FestEvent> allEvents = eventRepository.findAllByOrderByEventDateAsc();
        List<Ticket> allTickets = ticketRepository.findAll();

        long totalEvents = allEvents.size();
        long activeEvents = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.ACTIVE)
                .count();

        long totalIssued = allTickets.stream()
                .filter(t -> t.getStatus() != TicketStatus.CANCELLED)
                .count();

        long totalCheckedIn = allTickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.USED)
                .count();

        long totalCapacity = allEvents.stream()
                .mapToLong(FestEvent::getCapacity)
                .sum();

        long totalAvailableSeats = Math.max(0, totalCapacity - totalIssued);

        List<EventResponse> eventResponses = festEventService.getAllEvents();

        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalEvents(totalEvents);
        stats.setActiveEvents(activeEvents);
        stats.setTotalTicketsIssued(totalIssued);
        stats.setTotalCheckedIn(totalCheckedIn);
        stats.setTotalAvailableSeats(totalAvailableSeats);
        stats.setEvents(eventResponses);
        stats.setRecentCheckIns(checkInService.getRecentCheckIns());

        return stats;
    }

    public ReportSummaryResponse getReports() {
        List<FestEvent> events = eventRepository.findAllByOrderByEventDateAsc();
        List<EventReportItem> reportItems = new ArrayList<>();

        long totalSold = 0;
        long totalCheckedIn = 0;
        double totalRevenue = 0.0;

        for (FestEvent event : events) {
            long sold = ticketRepository.countByEventIdAndStatusNot(event.getId(), TicketStatus.CANCELLED);
            long checked = ticketRepository.countByEventIdAndStatus(event.getId(), TicketStatus.USED);
            long remaining = Math.max(0, event.getCapacity() - sold);
            double pct = 0.0;
            if (event.getCapacity() > 0) {
                pct = Math.round(((double) checked / event.getCapacity()) * 1000.0) / 10.0;
            }
            double eventRevenue = sold * (event.getTicketPrice() != null ? event.getTicketPrice() : 0.0);

            EventReportItem item = new EventReportItem();
            item.setEventId(event.getId());
            item.setEventName(event.getEventName());
            item.setCapacity(event.getCapacity());
            item.setTicketsSold(sold);
            item.setCheckedIn(checked);
            item.setRemaining(remaining);
            item.setAttendancePercentage(pct);
            item.setTicketPrice(event.getTicketPrice());
            item.setRevenue(eventRevenue);

            reportItems.add(item);

            totalSold += sold;
            totalCheckedIn += checked;
            totalRevenue += eventRevenue;
        }

        double avgPrice = totalSold > 0 ? (totalRevenue / totalSold) : 0.0;
        avgPrice = Math.round(avgPrice * 100.0) / 100.0;

        ReportSummaryResponse summary = new ReportSummaryResponse();
        summary.setTotalTicketsSold(totalSold);
        summary.setTotalCheckedIn(totalCheckedIn);
        summary.setTotalRevenue(totalRevenue);
        summary.setAverageTicketPrice(avgPrice);
        summary.setEventReports(reportItems);

        return summary;
    }
}
