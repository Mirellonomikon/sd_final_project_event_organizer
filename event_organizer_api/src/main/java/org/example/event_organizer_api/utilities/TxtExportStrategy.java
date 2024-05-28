package org.example.event_organizer_api.utilities;

import org.example.event_organizer_api.entity.Ticket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TxtExportStrategy implements TicketExportStrategy{
    @Override
    public void export(List<Ticket> matches, OutputStream outputStream) throws IOException {

    }
}
