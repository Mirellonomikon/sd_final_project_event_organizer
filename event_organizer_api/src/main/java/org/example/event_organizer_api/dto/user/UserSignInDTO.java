package org.example.event_organizer_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSignInDTO {
    private String username;
    private String password;
}
