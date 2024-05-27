package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByOrganizerId(Integer id);
    List<Event> findAllByOrderByEventDateAsc();
    List<Event> findAllByNameContainingIgnoreCase(String name);
}
