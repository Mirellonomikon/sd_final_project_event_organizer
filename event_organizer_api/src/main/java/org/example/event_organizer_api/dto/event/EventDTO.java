package org.example.event_organizer_api.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String name;
    private String eventType;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer location;
    private BigDecimal price;
    private Integer organizer;
    private Integer onSale;
}
