package org.example.event_organizer_api.mapper;

import org.example.event_organizer_api.dto.event.EventDTO;
import org.example.event_organizer_api.entity.Event;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapper implements GenericMapper<Event, EventDTO> {

    private final ModelMapper modelMapper;

    @Autowired
    public EventMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Event toEntity(EventDTO eventDTO) {
        return modelMapper.map(eventDTO, Event.class);
    }

    @Override
    public EventDTO toDTO(Event event) {
        return modelMapper.map(event, EventDTO.class);
    }
}
