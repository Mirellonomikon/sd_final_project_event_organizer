package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Ticket;
import org.example.event_organizer_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByEvent(Event event);
    List<Ticket> findByUser(User user);
}
