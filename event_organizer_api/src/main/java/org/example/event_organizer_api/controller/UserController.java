package org.example.event_organizer_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.event_organizer_api.dto.user.*;
import org.example.event_organizer_api.entity.User;
import org.example.event_organizer_api.security.JwtUtil;
import org.example.event_organizer_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserSignUpDTO userSignUpDTO) throws Exception {
        User registeredUser = userService.registerUser(userSignUpDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserSignInDTO userSignInDTO) throws Exception {
        UserIdDTO user = userService.loginUser(userSignInDTO);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("user", user);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/update")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<User> updateCredentials(@RequestParam Integer userId, @RequestBody UserUpdateCredentialsDTO userUpdateCredentialsDTO) throws Exception {
        User updatedUser = userService.updateUserCredentials(userUpdateCredentialsDTO, userId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('ORGANIZER')")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable String role) {
        List<User> users = userService.findUserByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR') or (#userId == authentication.principal.id)")
    public ResponseEntity<User> getUser(@RequestParam Integer userId) throws IllegalArgumentException {
        User user = userService.findUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) throws Exception {
        User registeredUser = userService.addUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PutMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> updateUser(@RequestBody UserDTO userDTO, @RequestParam Integer id) {
        User updatedUser = userService.updateUser(userDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteUser(@RequestParam Integer id) throws IllegalArgumentException {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wishlist/add")
    @PreAuthorize("hasRole('CLIENT') and #userId == authentication.principal.id")
    public ResponseEntity<User> addEventToWishlist(@RequestParam Integer userId, @RequestParam Integer eventId) {
        User user = userService.addEventToWishlist(userId, eventId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/wishlist/remove")
    @PreAuthorize("hasRole('CLIENT') and #userId == authentication.principal.id")
    public ResponseEntity<User> removeEventFromWishlist(@RequestParam Integer userId, @RequestParam Integer eventId) {
        User user = userService.removeEventFromWishlist(userId, eventId);
        return ResponseEntity.ok(user);
    }
}

