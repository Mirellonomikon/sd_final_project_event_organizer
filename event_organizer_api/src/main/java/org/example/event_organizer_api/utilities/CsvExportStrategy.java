package org.example.event_organizer_api.utilities;

import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CsvExportStrategy implements TicketExportStrategy {
    @Override
    public void export(Ticket ticket, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write("Event Name,Event Date,Event Time,Location,Purchase Price\n");

        Event event = ticket.getEvent();
        String line = String.format("%s,%s,%s,%s,%.2f\n",
                event.getName(),
                event.getEventDate(),
                event.getEventTime() != null ? event.getEventTime().toString() : "N/A",
                event.getLocation().getName(),
                ticket.getPurchasePrice());
        writer.write(line);

        writer.flush();
    }
}
