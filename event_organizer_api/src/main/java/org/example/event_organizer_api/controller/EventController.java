package org.example.event_organizer_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.event_organizer_api.dto.event.EventDTO;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
        return ResponseEntity.ok(event);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event event = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('ORGANIZER') and @eventServiceImpl.isOrganizer(#id, authentication.principal.id))")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer id, @RequestBody EventDTO eventDTO) {
        Event updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizer/{organizerId}")
    @PreAuthorize("hasRole('ORGANIZER') and #organizerId == authentication.principal.id")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Integer organizerId) {
        List<Event> events = eventService.getEventsByOrganizer(organizerId);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/sale/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('ORGANIZER') and @eventServiceImpl.isOrganizer(#id, authentication.principal.id))")
    public ResponseEntity<Event> setEventOnSale(@PathVariable Integer id, @RequestParam Integer salePercent) {
        Event event = eventService.setEventOnSale(id, salePercent);
        return ResponseEntity.ok(event);
    }
}
