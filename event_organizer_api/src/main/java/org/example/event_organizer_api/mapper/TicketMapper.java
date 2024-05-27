package org.example.event_organizer_api.mapper;

import org.example.event_organizer_api.dto.ticket.TicketDTO;
import org.example.event_organizer_api.entity.Ticket;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper implements GenericMapper<Ticket, TicketDTO> {

    private final ModelMapper modelMapper;

    @Autowired
    public TicketMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Ticket toEntity(TicketDTO ticketDTO) {
        throw new UnsupportedOperationException("Direct conversion from TicketDTO to Ticket is not supported.");
    }

    @Override
    public TicketDTO toDTO(Ticket ticket) {
        return modelMapper.map(ticket, TicketDTO.class);
    }
}
