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

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

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

    @Override
    public void deleteLocation(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location not found with id: " + id));

        locationRepository.delete(location);
    }

    @Override
    public Optional<Location> getLocationById(Integer id) {
        return locationRepository.findById(id);
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
}

