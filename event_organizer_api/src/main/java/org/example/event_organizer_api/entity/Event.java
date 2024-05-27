package org.example.event_organizer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "tickets_available", nullable = false)
    private Integer ticketsAvailable;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    public Event(String name, String eventType, Instant eventDate, Location location, Integer ticketsAvailable, BigDecimal price, User organizer) {
        this.name = name;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.location = location;
        this.ticketsAvailable = ticketsAvailable;
        this.price = price;
        this.organizer = organizer;
    }
}