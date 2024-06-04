package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    Location addLocation(LocationDTO locationDTO);
    Location updateLocation(Integer id, LocationDTO locationDTO);
    void deleteLocation(Integer id);
    Optional<Location> getLocationById(Integer id);
    List<Location> getAllLocations();
}

