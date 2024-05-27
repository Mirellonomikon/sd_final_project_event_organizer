package org.example.event_organizer_api.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TicketDTO {
    private Integer id;
    private Integer userId;
    private Integer eventId;
}
