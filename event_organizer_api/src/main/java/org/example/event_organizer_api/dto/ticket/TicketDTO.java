package org.example.event_organizer_api.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TicketDTO {
    private Integer id;
    private Integer userId;
    private Integer eventId;
    private BigDecimal purchasePrice;
}
