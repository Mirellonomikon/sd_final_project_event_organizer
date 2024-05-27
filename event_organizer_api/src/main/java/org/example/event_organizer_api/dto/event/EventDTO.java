package org.example.event_organizer_api.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class EventDTO {
    private String name;
    private String eventType;
    private Instant eventDate;
    private Integer locationId;
    private Integer ticketsAvailable;
    private BigDecimal price;
    private Integer organizerId;
}
