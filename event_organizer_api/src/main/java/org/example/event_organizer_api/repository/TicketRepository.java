package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByEventId(int eventId);
    List<Ticket> findAllByUserId(int userId);

    @Query("SELECT t FROM Ticket t WHERE t.event.organizer.name = :organizerName")
    List<Ticket> findAllByOrganizerName(String organizerName);
}
