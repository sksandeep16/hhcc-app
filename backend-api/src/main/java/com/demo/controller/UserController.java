package com.demo.controller;

import com.demo.dto.ChangePasswordRequest;
import com.demo.dto.LoginRequest;
import com.demo.dto.LoginResponse;
import com.demo.dto.UpdateProfileRequest;
import com.demo.entity.User;
import com.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User authentication and registration")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Login", description = "Authenticate with username and password")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/users/login username='{}'", request.getUsername());
        return userService.login(request.getUsername(), request.getPassword())
                .map(u -> {
                    log.info("Login success for username='{}'", u.getUsername());
                    return ResponseEntity.ok((Object) new LoginResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole()));
                })
                .orElseGet(() -> {
                    log.warn("Login failed for username='{}'", request.getUsername());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                });
    }

    @Operation(summary = "Get profile", description = "Retrieve a user's profile by ID")
    @ApiResponse(responseCode = "200", description = "Profile returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return userService.findById(id)
                .map(u -> ResponseEntity.ok((Object) new LoginResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update profile", description = "Update username and email for a user")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate username/email")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @Valid @RequestBody UpdateProfileRequest req) {
        log.info("PUT /api/users/{} username='{}'", id, req.getUsername());
        User updated = userService.updateProfile(id, req.getUsername(), req.getEmail());
        return ResponseEntity.ok(new LoginResponse(updated.getId(), updated.getUsername(), updated.getEmail(), updated.getRole()));
    }

    @Operation(summary = "Change password", description = "Change the password for a user")
    @ApiResponse(responseCode = "200", description = "Password changed")
    @ApiResponse(responseCode = "400", description = "Current password is incorrect")
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
                                            @Valid @RequestBody ChangePasswordRequest req) {
        log.info("PUT /api/users/{}/password", id);
        userService.changePassword(id, req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Register", description = "Register a new user account")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Username or email already exists / validation error")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        log.info("POST /api/users/register username='{}'", user.getUsername());
        if (userService.existsByUsername(user.getUsername())) {
            log.warn("Registration failed: username='{}' already exists", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        if (userService.existsByEmail(user.getEmail())) {
            log.warn("Registration failed: email='{}' already exists", user.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        User saved = userService.register(user);
        log.info("User registered id={} username='{}'", saved.getId(), saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
