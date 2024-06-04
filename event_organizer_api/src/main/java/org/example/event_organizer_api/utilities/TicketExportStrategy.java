package org.example.event_organizer_api.utilities;

import org.example.event_organizer_api.entity.Ticket;

import java.io.IOException;
import java.io.OutputStream;

public interface TicketExportStrategy {
    void export(Ticket ticket, OutputStream outputStream) throws IOException;
}
