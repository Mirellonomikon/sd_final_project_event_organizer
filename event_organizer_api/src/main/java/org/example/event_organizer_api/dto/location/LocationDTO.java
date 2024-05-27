package org.example.event_organizer_api.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationDTO {
    private String name;
    private String address;
    private Integer capacity;
}
