package com.festpass.repository;

import com.festpass.entity.EventStatus;
import com.festpass.entity.FestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FestEventRepository extends JpaRepository<FestEvent, Long> {
    List<FestEvent> findByStatus(EventStatus status);
    List<FestEvent> findAllByOrderByEventDateAsc();
}
