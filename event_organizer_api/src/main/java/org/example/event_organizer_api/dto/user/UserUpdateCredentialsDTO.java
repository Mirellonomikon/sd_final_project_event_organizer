package org.example.event_organizer_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateCredentialsDTO {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String name;
    private String email;
}
