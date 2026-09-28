package com.festpass;

import com.festpass.dto.*;
import com.festpass.entity.EventStatus;
import com.festpass.exception.EventCapacityExceededException;
import com.festpass.exception.InvalidQrCodeException;
import com.festpass.exception.TicketAlreadyUsedException;
import com.festpass.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class FestPassApplicationTests {

    @Autowired
    private FestEventService festEventService;

    @Autowired
    private AttendeeService attendeeService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private QrCodeService qrCodeService;

    @Test
    @DisplayName("Verify Full FestPass Business Flow: Event, Attendee, Ticket, QR, Check-in, One-time validation, Capacity limit, and Reports")
    void testFullFestPassFlow() {
        // 1. Create Event
        EventRequest eventReq = new EventRequest();
        eventReq.setEventName("HackSummit 2026");
        eventReq.setDescription("Annual 24-hour coding hackathon");
        eventReq.setEventDate(LocalDate.of(2026, 11, 10));
        eventReq.setVenue("Tech Lab 4");
        eventReq.setCapacity(2); // Small capacity to test limit enforcement
        eventReq.setTicketPrice(250.0);
        eventReq.setStatus(EventStatus.ACTIVE);

        EventResponse createdEvent = festEventService.createEvent(eventReq);
        assertNotNull(createdEvent.getId());
        assertEquals("HackSummit 2026", createdEvent.getEventName());
        assertEquals(2, createdEvent.getCapacity());

        // 2. Create Attendees
        AttendeeRequest attReq1 = new AttendeeRequest();
        attReq1.setName("Alice Cooper");
        attReq1.setEmail("alice@college.edu");
        attReq1.setPhone("+91 9123456780");
        attReq1.setCollegeName("State Tech Institute");
        attReq1.setStudentId("STI-2024-001");
        AttendeeResponse attendee1 = attendeeService.createAttendee(attReq1);
        assertNotNull(attendee1.getId());

        AttendeeRequest attReq2 = new AttendeeRequest();
        attReq2.setName("Bob Dylan");
        attReq2.setEmail("bob@college.edu");
        attReq2.setPhone("+91 9123456781");
        attReq2.setCollegeName("State Tech Institute");
        attReq2.setStudentId("STI-2024-002");
        AttendeeResponse attendee2 = attendeeService.createAttendee(attReq2);
        assertNotNull(attendee2.getId());

        AttendeeRequest attReq3 = new AttendeeRequest();
        attReq3.setName("Charlie Chaplin");
        attReq3.setEmail("charlie@college.edu");
        attReq3.setPhone("+91 9123456782");
        attReq3.setCollegeName("State Tech Institute");
        attReq3.setStudentId("STI-2024-003");
        AttendeeResponse attendee3 = attendeeService.createAttendee(attReq3);
        assertNotNull(attendee3.getId());

        // 3. Issue Ticket 1 (Capacity 1/2)
        TicketResponse ticket1 = ticketService.issueTicket(new TicketIssueRequest(createdEvent.getId(), attendee1.getId()));
        assertNotNull(ticket1.getId());
        assertNotNull(ticket1.getTicketNumber());
        assertTrue(ticket1.getTicketNumber().startsWith("FP-"));
        assertNotNull(ticket1.getQrCode());
        assertTrue(ticket1.getQrCode().startsWith("FP-QR-"));

        // 4. Generate QR code image check
        byte[] qrBytes = qrCodeService.generateQrCodeImage(ticket1.getQrCode(), 200, 200);
        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);

        // 5. Issue Ticket 2 (Capacity 2/2 - Now full)
        TicketResponse ticket2 = ticketService.issueTicket(new TicketIssueRequest(createdEvent.getId(), attendee2.getId()));
        assertNotNull(ticket2.getId());

        // 6. Test Capacity Limit Enforcement (Attempting Ticket 3 must be REJECTED)
        assertThrows(EventCapacityExceededException.class, () -> {
            ticketService.issueTicket(new TicketIssueRequest(createdEvent.getId(), attendee3.getId()));
        }, "Ticket sales closed. Event capacity has been reached.");

        // 7. Check In Valid Ticket 1
        CheckInResponse checkIn1 = checkInService.checkIn(new CheckInRequest(ticket1.getQrCode()));
        assertTrue(checkIn1.isSuccess());
        assertEquals("Check-in successful", checkIn1.getMessage());
        assertEquals("Alice Cooper", checkIn1.getAttendeeName());
        assertEquals("HackSummit 2026", checkIn1.getEventName());

        // 8. Try Same QR Code Again -> MUST BE REJECTED (One-time check-in rule)
        assertThrows(TicketAlreadyUsedException.class, () -> {
            checkInService.checkIn(new CheckInRequest(ticket1.getQrCode()));
        }, "Ticket has already been used.");

        // 9. Try Invalid QR Code -> MUST BE REJECTED
        assertThrows(InvalidQrCodeException.class, () -> {
            checkInService.checkIn(new CheckInRequest("INVALID-NONEXISTENT-TOKEN"));
        }, "Invalid QR code. No valid ticket was found.");

        // 10. Check Attendance Metrics
        AttendanceResponse attendance = festEventService.getEventAttendance(createdEvent.getId());
        assertEquals(2, attendance.getCapacity());
        assertEquals(2, attendance.getTicketsIssued());
        assertEquals(1, attendance.getCheckedIn());
        assertEquals(0, attendance.getRemaining());
        assertEquals(50.0, attendance.getAttendancePercentage());

        // 11. Check Reports & Revenue
        ReportSummaryResponse reports = reportService.getReports();
        assertTrue(reports.getTotalTicketsSold() >= 2);
        assertTrue(reports.getTotalRevenue() >= 500.0);
    }
}
