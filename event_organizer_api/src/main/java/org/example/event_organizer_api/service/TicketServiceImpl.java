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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service implementation for managing tickets.
 */
@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketMapper ticketMapper;

    /**
     * Constructor for TicketServiceImpl.
     *
     * @param ticketRepository the ticket repository
     * @param userRepository the user repository
     * @param eventRepository the event repository
     * @param ticketMapper the ticket mapper
     */
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, EventRepository eventRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketMapper = ticketMapper;
    }

    /**
     * Adds tickets for a given event.
     *
     * @param ticketDTO the ticket data transfer object
     * @param quantity the number of tickets to add
     * @return the list of created tickets
     */
    @Override
    public List<Ticket> addTickets(TicketDTO ticketDTO, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(ticketDTO.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + ticketDTO.getUserId()));

        Event event = eventRepository.findById(ticketDTO.getEventId())
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + ticketDTO.getEventId()));

        if (event.getTicketsAvailable() < quantity) {
            throw new IllegalArgumentException("Not enough tickets available for the event");
        }

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = ticketMapper.toEntity(ticketDTO);
            ticket.setUser(user);
            ticket.setEvent(event);
            tickets.add(ticket);
        }

        event.setTicketsAvailable(event.getTicketsAvailable() - quantity);
        eventRepository.save(event);
        return ticketRepository.saveAll(tickets);
    }

    /**
     * Updates an existing ticket.
     *
     * @param id the ticket ID
     * @param ticketDTO the ticket data transfer object
     * @return the updated ticket
     */
    @Override
    public Ticket updateTicket(Integer id, TicketDTO ticketDTO) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + id));

        User user = userRepository.findById(ticketDTO.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + ticketDTO.getUserId()));

        Event event = eventRepository.findById(ticketDTO.getEventId())
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + ticketDTO.getEventId()));

        ticket.setUser(user);
        ticket.setEvent(event);
        ticket.setPurchasePrice(ticketDTO.getPurchasePrice());

        return ticketRepository.save(ticket);
    }

    /**
     * Deletes a ticket.
     *
     * @param id the ticket ID
     */
    @Override
    public void deleteTicket(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + id));

        Event event = ticket.getEvent();
        event.setTicketsAvailable(event.getTicketsAvailable() + 1);
        eventRepository.save(event);

        ticketRepository.delete(ticket);
    }

    /**
     * Retrieves a ticket by its ID.
     *
     * @param id the ticket ID
     * @return the ticket, if found
     */
    @Override
    public Ticket getTicketById(Integer id) {
        return ticketRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + id));
    }

    /**
     * Retrieves all tickets.
     *
     * @return a list of all tickets
     */
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Retrieves all tickets for a specific user.
     *
     * @param id the user ID
     * @return a list of tickets for the user
     */
    @Override
    public List<Ticket> getAllTicketsByUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        return ticketRepository.findByUser(user);
    }

    /**
     * Retrieves all tickets for a specific event.
     *
     * @param id the event ID
     * @return a list of tickets for the event
     */
    @Override
    public List<Ticket> getAllTicketsByEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + id));

        return ticketRepository.findByEvent(event);
    }

    /**
     * Exports a ticket using a specified strategy.
     *
     * @param ticket the ticket to export
     * @param outputStream the output stream to write the exported ticket to
     * @param strategy the export strategy to use
     * @throws IOException if an I/O error occurs during export
     */
    @Override
    public void exportTicket(Ticket ticket, OutputStream outputStream, TicketExportStrategy strategy) throws IOException {
        strategy.export(ticket, outputStream);
    }
}



