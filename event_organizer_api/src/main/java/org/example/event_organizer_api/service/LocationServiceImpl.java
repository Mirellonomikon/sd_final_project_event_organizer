package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.mapper.LocationMapper;
import org.example.event_organizer_api.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service implementation for managing locations.
 */
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    /**
     * Constructor for LocationServiceImpl.
     *
     * @param locationRepository the location repository
     * @param locationMapper the location mapper
     */
    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * Adds a new location.
     *
     * @param locationDTO the location data transfer object
     * @return the created location
     */
    @Override
    public Location addLocation(LocationDTO locationDTO) {
        if (locationDTO.getName() == null || locationDTO.getName().isEmpty() ||
                locationDTO.getAddress() == null || locationDTO.getAddress().isEmpty() ||
                locationDTO.getCapacity() == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }

        Location location = locationMapper.toEntity(locationDTO);
        try {
            return locationRepository.save(location);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Location with the same name already exists");
        }
    }

    /**
     * Updates an existing location.
     *
     * @param id the location ID
     * @param locationDTO the location data transfer object
     * @return the updated location
     */
    @Override
    public Location updateLocation(Integer id, LocationDTO locationDTO) {
        if (locationDTO.getName() == null || locationDTO.getName().isEmpty() ||
                locationDTO.getAddress() == null || locationDTO.getAddress().isEmpty() ||
                locationDTO.getCapacity() == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location not found with id: " + id));

        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        location.setCapacity(locationDTO.getCapacity());

        try {
            return locationRepository.save(location);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Location with the same name already exists");
        }
    }

    /**
     * Deletes a location.
     *
     * @param id the location ID
     */
    @Override
    public void deleteLocation(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location not found with id: " + id));

        locationRepository.delete(location);
    }

    /**
     * Retrieves a location by its ID.
     *
     * @param id the location ID
     * @return the location, if found
     */
    @Override
    public Optional<Location> getLocationById(Integer id) {
        return locationRepository.findById(id);
    }

    /**
     * Retrieves all locations.
     *
     * @return a list of all locations
     */
    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
}
