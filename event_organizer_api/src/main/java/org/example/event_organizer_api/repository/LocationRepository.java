package org.example.event_organizer_api.repository;

import org.example.event_organizer_api.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
