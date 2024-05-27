package org.example.event_organizer_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateCredentialsDTO {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String name;
}
