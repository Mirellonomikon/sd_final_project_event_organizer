package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.ticket.TicketDTO;
import org.example.event_organizer_api.entity.Ticket;
import org.example.event_organizer_api.utilities.TicketExportStrategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface TicketService {
    List<Ticket> addTickets(TicketDTO ticketDTO, int quantity);
    Ticket updateTicket(Integer id, TicketDTO ticketDTO);
    void deleteTicket(Integer id);
    Ticket getTicketById(Integer id);
    List<Ticket> getAllTickets();
    List<Ticket> getAllTicketsByUser(Integer id);
    List<Ticket> getAllTicketsByEvent(Integer id);
    void exportTicket(Ticket ticket, OutputStream outputStream, TicketExportStrategy strategy) throws IOException;
}

