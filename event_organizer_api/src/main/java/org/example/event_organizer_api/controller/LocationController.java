package org.example.event_organizer_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.event_organizer_api.dto.location.LocationDTO;
import org.example.event_organizer_api.entity.Location;
import org.example.event_organizer_api.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
        Location location = locationService.getLocationById(id)
                .orElseThrow(() -> new NoSuchElementException("Location not found with id: " + id));
        return ResponseEntity.ok(location);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Location> addLocation(@RequestBody LocationDTO locationDTO) {
        Location location = locationService.addLocation(locationDTO);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Location> updateLocation(@PathVariable Integer id, @RequestBody LocationDTO locationDTO) {
        Location updatedLocation = locationService.updateLocation(id, locationDTO);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok().build();
    }
}

