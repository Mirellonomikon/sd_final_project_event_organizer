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

        modelMapper.createTypeMap(UserDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setId);
            mapper.skip(User::setWishlistEvents);
        });

        modelMapper.createTypeMap(UserSignUpDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setUserType);
            mapper.skip(User::setId);
            mapper.skip(User::setWishlistEvents);
        });

        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        Converter<UserUpdateCredentialsDTO, User> userCredentialsUpdateConverter = context -> {
            UserUpdateCredentialsDTO source = context.getSource();
            User destination = new User();
            destination.setUsername(source.getUsername());
            destination.setName(source.getName());
            destination.setEmail(source.getEmail());
            destination.setPassword(source.getNewPassword());
            return destination;
        };

        modelMapper.createTypeMap(UserUpdateCredentialsDTO.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setWishlistEvents))
                .setConverter(userCredentialsUpdateConverter);

        modelMapper.typeMap(LocationDTO.class, Location.class).addMappings(mapper -> {
            mapper.skip(Location::setId);
        });

        modelMapper.typeMap(Ticket.class, TicketDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getId(), TicketDTO::setUserId);
            mapper.map(src -> src.getEvent().getId(), TicketDTO::setEventId);
        });

        Converter<TicketDTO, Ticket> ticketDtoToTicketConverter = context -> {
            TicketDTO source = context.getSource();
            Ticket destination = new Ticket();
            if (source != null) {
                destination.setPurchasePrice(source.getPurchasePrice());
                destination.setUser(null);
                destination.setEvent(null);
            }
            return destination;
        };

        modelMapper.createTypeMap(TicketDTO.class, Ticket.class).setConverter(ticketDtoToTicketConverter);

        modelMapper.typeMap(EventDTO.class, Event.class).addMappings(mapper -> {
            mapper.skip(Event::setId);
            mapper.skip(Event::setUsersWishlist);
        });

        modelMapper.typeMap(Event.class, EventDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getOrganizer().getId(), EventDTO::setOrganizer);
            mapper.map(src -> src.getLocation().getId(), EventDTO::setLocation);
        });

        return modelMapper;
    }
}


