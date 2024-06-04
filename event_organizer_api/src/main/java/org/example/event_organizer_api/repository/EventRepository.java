package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByOrganizer(User organizer);
    List<Event> findByLocation(Location location);
    Optional<Event> findByName(String name);
    List<Event> findByEventType(String eventType);
}
