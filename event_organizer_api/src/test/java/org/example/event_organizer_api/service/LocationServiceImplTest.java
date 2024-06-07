package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.mapper.LocationMapper;
import org.example.event_organizer_api.repository.LocationRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl locationServiceImpl;

    private LocationDTO locationDTO;
    private Location location;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        locationDTO = new LocationDTO();
        locationDTO.setName("Test Location");
        locationDTO.setAddress("Test Address");
        locationDTO.setCapacity(100);

        location = new Location();
        location.setId(1);
        location.setName("Test Location");
        location.setAddress("Test Address");
        location.setCapacity(100);
    }

    @Test
    void addLocation_shouldAddLocation_whenValidLocationDTO() {
        when(locationMapper.toEntity(any(LocationDTO.class))).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Location result = locationServiceImpl.addLocation(locationDTO);

        assertNotNull(result);
        assertEquals(location.getName(), result.getName());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void addLocation_shouldThrowException_whenLocationDTOInvalid() {
        locationDTO.setName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            locationServiceImpl.addLocation(locationDTO);
        });

        assertEquals("All fields must be filled.", exception.getMessage());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void addLocation_shouldThrowException_whenDuplicateLocationName() {
        when(locationMapper.toEntity(any(LocationDTO.class))).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenThrow(new DataIntegrityViolationException(""));

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            locationServiceImpl.addLocation(locationDTO);
        });

        assertEquals("Location with the same name already exists", exception.getMessage());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void updateLocation_shouldUpdateLocation_whenValidLocationDTO() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Location result = locationServiceImpl.updateLocation(1, locationDTO);

        assertNotNull(result);
        assertEquals(location.getName(), result.getName());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void updateLocation_shouldThrowException_whenLocationDTOInvalid() {
        locationDTO.setName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            locationServiceImpl.updateLocation(1, locationDTO);
        });

        assertEquals("All fields must be filled.", exception.getMessage());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_shouldThrowException_whenLocationNotFound() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            locationServiceImpl.updateLocation(1, locationDTO);
        });

        assertEquals("Location not found with id: 1", exception.getMessage());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_shouldThrowException_whenDuplicateLocationName() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenThrow(new DataIntegrityViolationException(""));

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            locationServiceImpl.updateLocation(1, locationDTO);
        });

        assertEquals("Location with the same name already exists", exception.getMessage());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void deleteLocation_shouldDeleteLocation_whenLocationExists() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));

        locationServiceImpl.deleteLocation(1);

        verify(locationRepository, times(1)).delete(any(Location.class));
    }

    @Test
    void deleteLocation_shouldThrowException_whenLocationNotFound() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            locationServiceImpl.deleteLocation(1);
        });

        assertEquals("Location not found with id: 1", exception.getMessage());
        verify(locationRepository, never()).delete(any(Location.class));
    }

    @Test
    void getLocationById_shouldReturnLocation_whenLocationExists() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));

        Optional<Location> result = locationServiceImpl.getLocationById(1);

        assertTrue(result.isPresent());
        assertEquals(location.getName(), result.get().getName());
        verify(locationRepository, times(1)).findById(anyInt());
    }

    @Test
    void getLocationById_shouldReturnEmpty_whenLocationNotFound() {
        when(locationRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Location> result = locationServiceImpl.getLocationById(1);

        assertFalse(result.isPresent());
        verify(locationRepository, times(1)).findById(anyInt());
    }

    @Test
    void getAllLocations_shouldReturnAllLocations() {
        when(locationRepository.findAll()).thenReturn(Collections.singletonList(location));

        List<Location> result = locationServiceImpl.getAllLocations();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(locationRepository, times(1)).findAll();
    }
}
