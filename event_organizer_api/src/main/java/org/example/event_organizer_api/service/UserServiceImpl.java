package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.user.*;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.mapper.UserMapper;
import org.example.event_organizer_api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.event_organizer_api.repository.UserRepository;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventRepository eventRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
    }

    private void validateUserCredentials(String username, String name, String password, String email) {
        if (username == null || username.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                password == null || password.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Username, name, email and password cannot be empty.");
        }
    }

    @Override
    public User registerUser(UserSignUpDTO userSignUpDTO) throws DataIntegrityViolationException {
        validateUserCredentials(userSignUpDTO.getUsername(), userSignUpDTO.getName(), userSignUpDTO.getPassword(), userSignUpDTO.getEmail());
        if (userRepository.findByUsername(userSignUpDTO.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists.");
        }
        userSignUpDTO.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));
        User user = userMapper.signUpDtoToEntity(userSignUpDTO);
        return userRepository.save(user);
    }

    @Override
    public UserIdDTO loginUser(UserSignInDTO userSignInDTO) throws NoSuchElementException, IllegalArgumentException {
        User user = userRepository.findByUsername(userSignInDTO.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!passwordEncoder.matches(userSignInDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        return userMapper.toIdDTO(user);
    }

    @Override
    public User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws NoSuchElementException, IllegalArgumentException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        validateUserCredentials(userUpdateCredentialsDTO.getUsername(), userUpdateCredentialsDTO.getName(), userUpdateCredentialsDTO.getNewPassword(), userUpdateCredentialsDTO.getEmail());
        if (!passwordEncoder.matches(userUpdateCredentialsDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password.");
        }

        Optional<User> existingUser = userRepository.findByUsername(userUpdateCredentialsDTO.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already in use by another account.");
        }

        Optional<User> existingName = userRepository.findByName(userUpdateCredentialsDTO.getName());
        if (existingName.isPresent() && !existingName.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Name already in use by another account.");
        }

        user.setUsername(userUpdateCredentialsDTO.getUsername());
        user.setName(userUpdateCredentialsDTO.getName());
        user.setPassword(passwordEncoder.encode(userUpdateCredentialsDTO.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findUserByRole(String role) {
        return userRepository.findByUserType(role);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(UserDTO userDTO) throws DataIntegrityViolationException {
        validateUserCredentials(userDTO.getUsername(), userDTO.getName(), userDTO.getPassword(), userDTO.getEmail());
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists.");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserDTO userDTO, Integer id) throws NoSuchElementException{
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        validateUserCredentials(userDTO.getUsername(), userDTO.getName(), userDTO.getPassword(), userDTO.getEmail());

        Optional<User> userWithSameUsername = userRepository.findByUsername(userDTO.getUsername());
        if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username already in use by another account.");
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setName(userDTO.getName());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setUserType(userDTO.getUserType());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Integer userId) throws NoSuchElementException{
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User addEventToWishlist(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + eventId));

        user.getWishlistEvents().add(event);
        return userRepository.save(user);
    }

    @Override
    public User removeEventFromWishlist(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + eventId));

        if (user.getWishlistEvents().contains(event)) {
            user.getWishlistEvents().remove(event);
        } else {
            throw new NoSuchElementException("Event not found in user's wishlist.");
        }

        return userRepository.save(user);
    }
}
