package com.festpass.repository;

import com.festpass.entity.Ticket;
import com.festpass.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByQrCode(String qrCode);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByEventId(Long eventId);

    List<Ticket> findByAttendeeId(Long attendeeId);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, TicketStatus status);

    long countByEventIdAndStatusNot(Long eventId, TicketStatus status);

    List<Ticket> findAllByOrderByIssuedAtDesc();

    List<Ticket> findTop10ByStatusOrderByCheckedInAtDesc(TicketStatus status);
}
