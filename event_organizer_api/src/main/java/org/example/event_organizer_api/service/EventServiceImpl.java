package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.event.EventDTO;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.mapper.EventMapper;
import org.example.event_organizer_api.repository.EventRepository;
import org.example.event_organizer_api.repository.LocationRepository;
import org.example.event_organizer_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final EmailService emailService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, LocationRepository locationRepository, EventMapper eventMapper, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.eventMapper = eventMapper;
        this.emailService = emailService;
    }

    @Override
    public Event createEvent(EventDTO eventDTO) {
        Event event = eventMapper.toEntity(eventDTO);

        User organizer = userRepository.findById(eventDTO.getOrganizer())
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + eventDTO.getOrganizer()));
        event.setOrganizer(organizer);

        Location location = locationRepository.findById(eventDTO.getLocation())
                .orElseThrow(() -> new NoSuchElementException("Location not found with ID: " + eventDTO.getLocation()));
        event.setLocation(location);

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Integer id, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));

        existingEvent.setName(eventDTO.getName());
        existingEvent.setEventType(eventDTO.getEventType());
        existingEvent.setEventDate(eventDTO.getEventDate());
        existingEvent.setEventTime(eventDTO.getEventTime());

        Location location = locationRepository.findById(eventDTO.getLocation())
                .orElseThrow(() -> new NoSuchElementException("Location not found with ID: " + eventDTO.getLocation()));
        existingEvent.setLocation(location);

        existingEvent.setTicketsAvailable(eventDTO.getTicketsAvailable());
        existingEvent.setPrice(eventDTO.getPrice());

        User organizer = userRepository.findById(eventDTO.getOrganizer())
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + eventDTO.getOrganizer()));
        existingEvent.setOrganizer(organizer);

        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));
        eventRepository.delete(event);
    }

    @Override
    public Optional<Event> getEventById(Integer id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getEventsByOrganizer(Integer organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + organizerId));
        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    public Event setEventOnSale(Integer id, Integer salePercent) {
        if (salePercent < 0 || salePercent > 100) {
            throw new IllegalArgumentException("Sale percent must be between 0 and 100");
        }

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));

        BigDecimal originalPrice = event.getPrice().divide(BigDecimal.valueOf(1 - event.getOnSale() / 100.0), 2, RoundingMode.HALF_UP);
        BigDecimal newPrice = originalPrice.multiply(BigDecimal.valueOf(1 - salePercent / 100.0)).setScale(2, RoundingMode.HALF_UP);

        if (salePercent < event.getOnSale()) {
            notifyUsersOfSale(event);
        }

        event.setOnSale(salePercent);
        event.setPrice(newPrice);

        return eventRepository.save(event);
    }

    private void notifyUsersOfSale(Event event) {
        Set<User> users = event.getUsersWishlist();
        for (User user : users) {
            String message = String.format("The event '%s' is now on sale! New price: %.2f", event.getName(), event.getPrice());
            emailService.notifyUser(user.getEmail(), "Event on Sale", message);
        }
    }

    public boolean isOrganizer(Integer eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + eventId));
        return event.getOrganizer().getId().equals(userId);
    }
}




