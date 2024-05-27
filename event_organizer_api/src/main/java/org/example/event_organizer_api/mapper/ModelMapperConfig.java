package org.example.event_organizer_api.mapper;

import org.example.event_organizer_api.dto.event.EventDTO;
import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.dto.ticket.TicketDTO;
import org.example.event_organizer_api.dto.user.UserDTO;
import org.example.event_organizer_api.dto.user.UserSignUpDTO;
import org.example.event_organizer_api.dto.user.UserUpdateCredentialsDTO;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.entity.Ticket;
import org.example.event_organizer_api.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        modelMapper.typeMap(Event.class, EventDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getLocation().getId(), EventDTO::setLocationId);
            mapper.map(src -> src.getOrganizer().getId(), EventDTO::setOrganizerId);
        });

        Converter<EventDTO, Event> eventDtoToEventConverter = context -> {
            EventDTO source = context.getSource();
            Event destination = new Event();
            if (source != null) {
                destination.setName(source.getName());
                destination.setEventType(source.getEventType());
                destination.setEventDate(source.getEventDate());
                destination.setPrice(source.getPrice());
                destination.setLocation(null);
                destination.setOrganizer(null);
            }
            return destination;
        };

        modelMapper.createTypeMap(EventDTO.class, Event.class).setConverter(eventDtoToEventConverter);

        modelMapper.typeMap(Ticket.class, TicketDTO.class).addMappings(mapper -> {
            mapper.map(Ticket::getId, TicketDTO::setId);
            mapper.map(src -> src.getUser().getId(), TicketDTO::setUserId);
            mapper.map(src -> src.getEvent().getId(), TicketDTO::setEventId);
        });

        modelMapper.typeMap(LocationDTO.class, Location.class).addMappings(mapper -> {
            mapper.skip(Location::setId);
        });

        modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setId);
        });

        modelMapper.typeMap(UserSignUpDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setUserType);
            mapper.skip(User::setId);
        });

        Converter<UserUpdateCredentialsDTO, User> userCredentialsUpdateConverter = context -> {
            UserUpdateCredentialsDTO source = context.getSource();
            User destination = new User();
            destination.setUsername(source.getUsername());
            destination.setName(source.getName());
            destination.setPassword(source.getNewPassword());
            return destination;
        };

        modelMapper.createTypeMap(UserUpdateCredentialsDTO.class, User.class)
                .setConverter(userCredentialsUpdateConverter);

        return modelMapper;
    }
}
