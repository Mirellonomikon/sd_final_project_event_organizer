package org.example.event_organizer_api.service;

import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.dto.event.EventDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(Integer id, EventDTO eventDTO);
    void deleteEvent(Integer id);
    Optional<Event> getEventById(Integer id);
    List<Event> getAllEvents();
    List<Event> getEventsByOrganizer(Integer organizerId);
    Event setEventOnSale(Integer id, Integer salePercent);
}

