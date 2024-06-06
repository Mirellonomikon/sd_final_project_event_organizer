package org.example.event_organizer_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdDTO {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String userType;
    private String email;
}
