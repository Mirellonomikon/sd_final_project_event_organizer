package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.user.UserSignInDTO;
import org.example.event_organizer_api.dto.user.UserSignUpDTO;
import org.example.event_organizer_api.dto.user.UserUpdateCredentialsDTO;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.dto.user.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserSignUpDTO userSignUpDTO) throws Exception;
    User loginUser(UserSignInDTO userSignInDTO) throws Exception;
    User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws Exception;
    Optional<User> findUserById(Integer id);
    Optional<User> findUserByUsername(String username);
    List<User> findUserByRole(String role);
    List<User> findAllUsers();
    User addUser(UserDTO userDTO) throws Exception;
    User updateUser(UserDTO userDTO, Integer id);
    void deleteUser(Integer userId);
    User addEventToWishlist(Integer userId, Integer eventId);
    User removeEventFromWishlist(Integer userId, Integer eventId);
}
