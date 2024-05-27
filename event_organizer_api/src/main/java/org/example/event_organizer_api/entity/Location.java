package org.example.event_organizer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false, length = Integer.MAX_VALUE)
    private String address;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    public Location(String name, String address, Integer capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }
}