package org.example.event_organizer_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.event_organizer_api.dto.ticket.TicketDTO;
import org.example.event_organizer_api.entity.Ticket;
import org.example.event_organizer_api.service.TicketService;
import org.example.event_organizer_api.utilities.CsvExportStrategy;
import org.example.event_organizer_api.utilities.TicketExportStrategy;
import org.example.event_organizer_api.utilities.TxtExportStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT') and #ticketDTO.userId == authentication.principal.id")
    public ResponseEntity<List<Ticket>> createTickets(@RequestBody TicketDTO ticketDTO, @RequestParam int quantity) throws Exception {
        List<Ticket> tickets = ticketService.addTickets(ticketDTO, quantity);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('CLIENT') and #ticketDTO.userId == authentication.principal.id")
    public ResponseEntity<Ticket> updateTicket(@RequestParam Integer ticketId, @RequestBody TicketDTO ticketDTO) throws Exception {
        Ticket updatedTicket = ticketService.updateTicket(ticketId, ticketDTO);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') and @ticketServiceImpl.getTicketById(#id).user.id == authentication.principal.id")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) throws Exception {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER') or (@ticketServiceImpl.getTicketById(#id).user.id == authentication.principal.id)")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) throws Exception {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CLIENT') and #userId == authentication.principal.id")
    public ResponseEntity<List<Ticket>> getAllTicketsByUser(@PathVariable Integer userId) {
        List<Ticket> tickets = ticketService.getAllTicketsByUser(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<List<Ticket>> getAllTicketsByEvent(@PathVariable Integer eventId) {
        List<Ticket> tickets = ticketService.getAllTicketsByEvent(eventId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/export/{ticketId}")
    @PreAuthorize("hasRole('CLIENT') and @ticketServiceImpl.getTicketById(#ticketId).user.id == authentication.principal.id")
    public ResponseEntity<byte[]> exportTicket(@PathVariable Integer ticketId, @RequestParam String format) throws Exception {
        Ticket ticket = ticketService.getTicketById(ticketId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TicketExportStrategy strategy = format.equals("csv") ? new CsvExportStrategy() : new TxtExportStrategy();
        ticketService.exportTicket(ticket, outputStream, strategy);
        byte[] data = outputStream.toByteArray();
        return ResponseEntity.ok()
                .contentType(format.equals("csv") ? MediaType.valueOf("text/csv") : MediaType.valueOf("text/plain"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ticket." + format + "\"")
                .body(data);
    }
}
