package org.example.event_organizer_api.mapper;

import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.entity.Location;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper implements GenericMapper<Location, LocationDTO> {
    private final ModelMapper modelMapper;

    @Autowired
    public LocationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Location toEntity(LocationDTO locationDTO) {
        return modelMapper.map(locationDTO, Location.class);
    }

    @Override
    public LocationDTO toDTO(Location location) {
        return modelMapper.map(location, LocationDTO.class);
    }
}

