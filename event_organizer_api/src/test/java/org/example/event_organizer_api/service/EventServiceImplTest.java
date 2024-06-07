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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventServiceImpl eventServiceImpl;

    private EventDTO eventDTO;
    private Event event;
    private User user;
    private Location location;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        eventDTO = new EventDTO();
        eventDTO.setName("Test Event");
        eventDTO.setEventType("Type");
        eventDTO.setEventDate(LocalDate.parse("2024-01-01"));
        eventDTO.setEventTime(LocalTime.parse("18:00"));
        eventDTO.setLocation(1);
        eventDTO.setOrganizer(1);
        eventDTO.setOnSale(10);
        eventDTO.setPrice(new BigDecimal("50.00"));

        event = new Event();
        event.setId(1);
        event.setName("Test Event");
        event.setOnSale(10);
        event.setPrice(new BigDecimal("50.00"));

        user = new User();
        user.setId(1);
        user.setUsername("organizer");

        location = new Location();
        location.setId(1);
        location.setName("Location");
        location.setCapacity(100);
    }

    @Test
    void createEvent() {
        when(eventMapper.toEntity(any(EventDTO.class))).thenReturn(event);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventServiceImpl.createEvent(eventDTO);

        assertNotNull(result);
        assertEquals(event.getName(), result.getName());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(ticketRepository.findByEvent(any(Event.class))).thenReturn(List.of());
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventServiceImpl.updateEvent(1, eventDTO);

        assertNotNull(result);
        assertEquals(event.getName(), result.getName());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void deleteEvent() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).delete(any(Event.class));

        eventServiceImpl.deleteEvent(1);

        verify(eventRepository, times(1)).delete(any(Event.class));
    }

    @Test
    void getEventById() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));

        Optional<Event> result = eventServiceImpl.getEventById(1);

        assertTrue(result.isPresent());
        assertEquals(event.getName(), result.get().getName());
    }

    @Test
    void getAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventServiceImpl.getAllEvents();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getEventsByOrganizer() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.findByOrganizer(any(User.class))).thenReturn(List.of(event));

        List<Event> result = eventServiceImpl.getEventsByOrganizer(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void setEventOnSale() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventServiceImpl.setEventOnSale(1, 20);

        assertNotNull(result);
        assertEquals(20, result.getOnSale());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void isOrganizer() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        event.setOrganizer(user);

        boolean result = eventServiceImpl.isOrganizer(1, 1);

        assertTrue(result);
    }
}
