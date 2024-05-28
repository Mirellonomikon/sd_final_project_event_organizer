package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
