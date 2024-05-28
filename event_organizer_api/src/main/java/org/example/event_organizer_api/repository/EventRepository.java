package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
