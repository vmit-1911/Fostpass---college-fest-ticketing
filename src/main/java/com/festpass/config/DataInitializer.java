package com.festpass.config;

import com.festpass.entity.*;
import com.festpass.repository.AttendeeRepository;
import com.festpass.repository.FestEventRepository;
import com.festpass.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FestEventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketRepository ticketRepository;

    public DataInitializer(FestEventRepository eventRepository,
                           AttendeeRepository attendeeRepository,
                           TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void run(String... args) {
        // Prevent duplicate data creation on restart
        if (eventRepository.count() > 0) {
            log.info("FestPass database already initialized with data. Skipping sample data creation.");
            return;
        }

        log.info("Initializing FestPass sample data...");

        // 1. Events
        FestEvent techFest = new FestEvent(
                "TechFest 2026",
                "Annual National Level Technical Festival featuring hackathons, robotics, coding sprints, and guest lectures.",
                LocalDate.of(2026, 10, 15),
                "Main Auditorium",
                500,
                150.0,
                EventStatus.ACTIVE
        );
        eventRepository.save(techFest);

        FestEvent culturalFest = new FestEvent(
                "Cultural Fest 2026",
                "Grand Inter-College Cultural Celebration showcasing music bands, dance face-offs, drama, and art exhibitions.",
                LocalDate.of(2026, 11, 20),
                "Open Air Theatre",
                800,
                100.0,
                EventStatus.UPCOMING
        );
        eventRepository.save(culturalFest);

        FestEvent gamingFest = new FestEvent(
                "GameCon Arena 2026",
                "State-level E-sports Championship featuring Valorant, FIFA, and BGMI competitions with prize pool.",
                LocalDate.of(2026, 12, 5),
                "Indoor Sports Complex",
                300,
                200.0,
                EventStatus.ACTIVE
        );
        eventRepository.save(gamingFest);

        // 2. Attendees
        Attendee rahul = new Attendee("Rahul Kumar", "rahul.kumar@college.edu", "+91 9876543210", "Apex Institute of Technology", "AIT-2023-CS-042");
        Attendee priya = new Attendee("Priya Sharma", "priya.sharma@univ.ac.in", "+91 9876543211", "National University", "NU-2022-EC-108");
        Attendee arun = new Attendee("Arun Kumar", "arun.kumar@engg.edu", "+91 9876543212", "City Engineering College", "CEC-2024-IT-015");
        Attendee sneha = new Attendee("Sneha Raj", "sneha.raj@tech.edu", "+91 9876543213", "Metropolitan Technical Campus", "MTC-2023-AI-088");

        attendeeRepository.save(rahul);
        attendeeRepository.save(priya);
        attendeeRepository.save(arun);
        attendeeRepository.save(sneha);

        // 3. Sample Tickets
        // Ticket 1: ACTIVE (Ready to check-in)
        Ticket ticket1 = new Ticket(
                "FP-2026-000001",
                "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(),
                techFest,
                rahul,
                techFest.getTicketPrice(),
                TicketStatus.ACTIVE
        );
        ticketRepository.save(ticket1);

        // Ticket 2: USED (Checked in 45 minutes ago - ready to demonstrate duplicate check-in rejection)
        Ticket ticket2 = new Ticket(
                "FP-2026-000002",
                "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(),
                techFest,
                priya,
                techFest.getTicketPrice(),
                TicketStatus.USED
        );
        ticket2.setCheckedInAt(LocalDateTime.now().minusMinutes(45));
        ticketRepository.save(ticket2);

        // Ticket 3: ACTIVE (Cultural Fest)
        Ticket ticket3 = new Ticket(
                "FP-2026-000003",
                "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(),
                culturalFest,
                arun,
                culturalFest.getTicketPrice(),
                TicketStatus.ACTIVE
        );
        ticketRepository.save(ticket3);

        // Ticket 4: USED (GameCon Arena)
        Ticket ticket4 = new Ticket(
                "FP-2026-000004",
                "FP-QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(),
                gamingFest,
                sneha,
                gamingFest.getTicketPrice(),
                TicketStatus.USED
        );
        ticket4.setCheckedInAt(LocalDateTime.now().minusHours(2));
        ticketRepository.save(ticket4);

        log.info("FestPass sample data initialized successfully!");
        log.info("Sample Active Ticket QR Token (Rahul): {}", ticket1.getQrCode());
        log.info("Sample Used Ticket QR Token (Priya): {}", ticket2.getQrCode());
    }
}
