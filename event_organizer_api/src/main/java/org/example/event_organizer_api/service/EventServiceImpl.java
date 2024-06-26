package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.event.EventDTO;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.mapper.EventMapper;
import org.example.event_organizer_api.repository.EventRepository;
import org.example.event_organizer_api.repository.LocationRepository;
import org.example.event_organizer_api.repository.TicketRepository;
import org.example.event_organizer_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service implementation for managing events.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final TicketRepository ticketRepository;
    private final EventMapper eventMapper;
    private final EmailService emailService;

    /**
     * Constructor for EventServiceImpl.
     *
     * @param eventRepository the event repository
     * @param userRepository the user repository
     * @param locationRepository the location repository
     * @param ticketRepository the ticket repository
     * @param eventMapper the event mapper
     * @param emailService the email service
     */
    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, LocationRepository locationRepository, TicketRepository ticketRepository, EventMapper eventMapper, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.ticketRepository = ticketRepository;
        this.eventMapper = eventMapper;
        this.emailService = emailService;
    }

    /**
     * Creates a new event.
     *
     * @param eventDTO the event data transfer object
     * @return the created event
     */
    @Override
    public Event createEvent(EventDTO eventDTO) {
        Event event = eventMapper.toEntity(eventDTO);

        User organizer = userRepository.findById(eventDTO.getOrganizer())
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + eventDTO.getOrganizer()));
        event.setOrganizer(organizer);

        Location location = locationRepository.findById(eventDTO.getLocation())
                .orElseThrow(() -> new NoSuchElementException("Location not found with ID: " + eventDTO.getLocation()));
        event.setLocation(location);
        event.setTicketsAvailable(location.getCapacity());

        return eventRepository.save(event);
    }

    /**
     * Updates an existing event.
     *
     * @param id the event ID
     * @param eventDTO the event data transfer object
     * @return the updated event
     */
    @Override
    public Event updateEvent(Integer id, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));

        int ticketsSoldCount = ticketRepository.findByEvent(existingEvent).size();

        Location newLocation = locationRepository.findById(eventDTO.getLocation())
                .orElseThrow(() -> new NoSuchElementException("Location not found with ID: " + eventDTO.getLocation()));

        int newTicketsAvailable = newLocation.getCapacity() - ticketsSoldCount;

        if (newTicketsAvailable < 0) {
            throw new IllegalArgumentException("The new location does not have enough capacity for the tickets already sold.");
        }

        existingEvent.setName(eventDTO.getName());
        existingEvent.setEventType(eventDTO.getEventType());
        existingEvent.setEventDate(eventDTO.getEventDate());
        existingEvent.setEventTime(eventDTO.getEventTime());
        existingEvent.setLocation(newLocation);
        existingEvent.setTicketsAvailable(newTicketsAvailable);

        if (!Objects.equals(eventDTO.getOnSale(), existingEvent.getOnSale())) {
            if (eventDTO.getOnSale() < 0 || eventDTO.getOnSale() > 100) {
                throw new IllegalArgumentException("Sale percent must be between 0 and 100");
            }
            BigDecimal originalPrice = existingEvent.getPrice().divide(BigDecimal.valueOf(1 - existingEvent.getOnSale() / 100.0), 2, RoundingMode.HALF_UP);
            BigDecimal newPrice = originalPrice.multiply(BigDecimal.valueOf(1 - eventDTO.getOnSale() / 100.0)).setScale(2, RoundingMode.HALF_UP);
            existingEvent.setPrice(newPrice);
        } else {
            existingEvent.setPrice(eventDTO.getPrice());
        }

        existingEvent.setOnSale(eventDTO.getOnSale());

        User organizer = userRepository.findById(eventDTO.getOrganizer())
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + eventDTO.getOrganizer()));
        existingEvent.setOrganizer(organizer);

        return eventRepository.save(existingEvent);
    }

    /**
     * Deletes an event.
     *
     * @param id the event ID
     */
    @Override
    public void deleteEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));
        eventRepository.delete(event);
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param id the event ID
     * @return the event, if found
     */
    @Override
    public Optional<Event> getEventById(Integer id) {
        return eventRepository.findById(id);
    }

    /**
     * Retrieves all events.
     *
     * @return a list of all events
     */
    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Retrieves events organized by a specific user.
     *
     * @param organizerId the organizer ID
     * @return a list of events organized by the user
     */
    @Override
    public List<Event> getEventsByOrganizer(Integer organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new NoSuchElementException("Organizer not found with ID: " + organizerId));
        return eventRepository.findByOrganizer(organizer);
    }

    /**
     * Sets an event on sale.
     *
     * @param id the event ID
     * @param salePercent the sale percentage
     * @return the updated event
     */
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

    /**
     * Notifies users about the sale of an event.
     *
     * @param event the event that is on sale
     */
    private void notifyUsersOfSale(Event event) {
        Set<User> users = event.getUsersWishlist();
        for (User user : users) {
            String message = String.format("The event '%s' is now on sale! New price: %.2f", event.getName(), event.getPrice());
            emailService.notifyUser(user.getEmail(), "Event on Sale for " + user.getName(), message);
        }
    }

    /**
     * Checks if a user is the organizer of a specific event.
     *
     * @param eventId the event ID
     * @param userId the user ID
     * @return true if the user is the organizer of the event, false otherwise
     */
    public boolean isOrganizer(Integer eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + eventId));
        return event.getOrganizer().getId().equals(userId);
    }
}




