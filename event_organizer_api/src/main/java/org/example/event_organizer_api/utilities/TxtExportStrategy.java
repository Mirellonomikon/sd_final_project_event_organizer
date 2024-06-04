package org.example.event_organizer_api.utilities;

import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class TxtExportStrategy implements TicketExportStrategy {
    @Override
    public void export(Ticket ticket, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        Event event = ticket.getEvent();
        String block = String.format("Event: %s\nDate: %s\nTime: %s\nLocation: %s\nPurchase Price: %.2f\n\n",
                event.getName(),
                event.getEventDate(),
                event.getEventTime() != null ? event.getEventTime().toString() : "N/A",
                event.getLocation().getName(),
                ticket.getPurchasePrice());
        writer.write(block);

        writer.flush();
    }
}

