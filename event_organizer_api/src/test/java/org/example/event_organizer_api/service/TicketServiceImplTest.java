package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.ticket.TicketDTO;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Ticket;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.mapper.TicketMapper;
import org.example.event_organizer_api.repository.EventRepository;
import org.example.event_organizer_api.repository.TicketRepository;
import org.example.event_organizer_api.repository.UserRepository;
import org.example.event_organizer_api.utilities.TicketExportStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private TicketExportStrategy ticketExportStrategy;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    private TicketDTO ticketDTO;
    private Ticket ticket;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ticketDTO = new TicketDTO();
        ticketDTO.setUserId(1);
        ticketDTO.setEventId(1);
        ticketDTO.setPurchasePrice(BigDecimal.valueOf(100));

        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        event = new Event();
        event.setId(1);
        event.setName("Test Event");
        event.setTicketsAvailable(10);

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setUser(user);
        ticket.setEvent(event);
        ticket.setPurchasePrice(BigDecimal.valueOf(100));
    }

    @Test
    void addTickets() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(ticketMapper.toEntity(any(TicketDTO.class))).thenReturn(ticket);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        List<Ticket> result = ticketServiceImpl.addTickets(ticketDTO, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateTicket() {
        when(ticketRepository.findById(anyInt())).thenReturn(Optional.of(ticket));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketServiceImpl.updateTicket(1, ticketDTO);

        assertNotNull(result);
        assertEquals(ticket.getId(), result.getId());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void deleteTicket() {
        when(ticketRepository.findById(anyInt())).thenReturn(Optional.of(ticket));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        ticketServiceImpl.deleteTicket(1);

        verify(ticketRepository, times(1)).delete(any(Ticket.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void getTicketById() {
        when(ticketRepository.findById(anyInt())).thenReturn(Optional.of(ticket));

        Ticket result = ticketServiceImpl.getTicketById(1);

        assertNotNull(result);
        assertEquals(ticket.getId(), result.getId());
        verify(ticketRepository, times(1)).findById(anyInt());
    }

    @Test
    void getAllTickets() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        List<Ticket> result = ticketServiceImpl.getAllTickets();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getAllTicketsByUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(ticketRepository.findByUser(any(User.class))).thenReturn(List.of(ticket));

        List<Ticket> result = ticketServiceImpl.getAllTicketsByUser(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByUser(any(User.class));
    }

    @Test
    void getAllTicketsByEvent() {
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(ticketRepository.findByEvent(any(Event.class))).thenReturn(List.of(ticket));

        List<Ticket> result = ticketServiceImpl.getAllTicketsByEvent(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByEvent(any(Event.class));
    }

    @Test
    void exportTicket() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        doNothing().when(ticketExportStrategy).export(any(Ticket.class), any(OutputStream.class));

        ticketServiceImpl.exportTicket(ticket, outputStream, ticketExportStrategy);

        verify(ticketExportStrategy, times(1)).export(any(Ticket.class), any(OutputStream.class));
    }
}
