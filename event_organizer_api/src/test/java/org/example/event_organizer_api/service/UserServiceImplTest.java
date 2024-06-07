package org.example.event_organizer_api.service;

import org.example.event_organizer_api.dto.user.*;
import org.example.event_organizer_api.entity.Event;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.mapper.UserMapper;
import org.example.event_organizer_api.repository.EventRepository;
import org.example.event_organizer_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserSignUpDTO userSignUpDTO;
    private UserSignInDTO userSignInDTO;
    private UserUpdateCredentialsDTO userUpdateCredentialsDTO;
    private UserDTO userDTO;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setUsername("testuser");
        userSignUpDTO.setName("Test User");
        userSignUpDTO.setPassword("password");
        userSignUpDTO.setEmail("test@example.com");

        userSignInDTO = new UserSignInDTO();
        userSignInDTO.setUsername("testuser");
        userSignInDTO.setPassword("password");

        userUpdateCredentialsDTO = new UserUpdateCredentialsDTO();
        userUpdateCredentialsDTO.setUsername("testuser");
        userUpdateCredentialsDTO.setName("Test User");
        userUpdateCredentialsDTO.setNewPassword("newpassword");
        userUpdateCredentialsDTO.setOldPassword("password");
        userUpdateCredentialsDTO.setEmail("test@example.com");

        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setName("Test User");
        userDTO.setPassword("password");
        userDTO.setEmail("test@example.com");
        userDTO.setUserType("CLIENT");

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setPassword("password");
        user.setEmail("test@example.com");

        event = new Event();
        event.setId(1);
        event.setName("Test Event");
    }

    @Test
    void registerUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.signUpDtoToEntity(any(UserSignUpDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.registerUser(userSignUpDTO);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User result = userServiceImpl.loginUser(userSignInDTO);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void updateUserCredentials() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.updateUserCredentials(userUpdateCredentialsDTO, user.getId());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.findUserById(1);

        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    void findUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.findUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    void findUserByRole() {
        when(userRepository.findByUserType(anyString())).thenReturn(List.of(user));

        List<User> result = userServiceImpl.findUserByRole("CLIENT");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userServiceImpl.findAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void addUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.addUser(userDTO);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.updateUser(userDTO, user.getId());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyInt());

        userServiceImpl.deleteUser(1);

        verify(userRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void addEventToWishlist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.addEventToWishlist(1, 1);

        assertNotNull(result);
        assertTrue(result.getWishlistEvents().contains(event));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void removeEventFromWishlist() {
        user.getWishlistEvents().add(event);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImpl.removeEventFromWishlist(1, 1);

        assertNotNull(result);
        assertFalse(result.getWishlistEvents().contains(event));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserWishlistEvents() {
        user.getWishlistEvents().add(event);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        Set<Event> result = userServiceImpl.getUserWishlistEvents(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(event));
    }
}
