package com.demo.controller;

import com.demo.dto.ChangePasswordRequest;
import com.demo.dto.LoginRequest;
import com.demo.dto.UpdateProfileRequest;
import com.demo.entity.User;
import com.demo.exception.GlobalExceptionHandler;
import com.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Mock UserService userService;
    @InjectMocks UserController userController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private User john;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        john = new User();
        john.setId(1L);
        john.setUsername("john_doe");
        john.setEmail("john@example.com");
        john.setPassword("pass1234");
        john.setRole("USER");
    }

    // ── POST /login ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /login returns 200 with user data on valid credentials")
    void login_validCredentials_returns200() throws Exception {
        when(userService.login("john_doe", "pass1234")).thenReturn(Optional.of(john));

        LoginRequest req = new LoginRequest();
        req.setUsername("john_doe");
        req.setPassword("pass1234");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /login returns 401 on invalid credentials")
    void login_invalidCredentials_returns401() throws Exception {
        when(userService.login(anyString(), anyString())).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setUsername("john_doe");
        req.setPassword("wrong");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /register ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /register returns 201 when user is new")
    void register_newUser_returns201() throws Exception {
        when(userService.existsByUsername("john_doe")).thenReturn(false);
        when(userService.existsByEmail("john@example.com")).thenReturn(false);
        when(userService.register(any(User.class))).thenReturn(john);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(john)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    @DisplayName("POST /register returns 400 when username already exists")
    void register_existingUsername_returns400() throws Exception {
        User req = new User();
        req.setUsername("john_doe");
        req.setEmail("other@example.com");
        req.setPassword("pass1234");

        when(userService.existsByUsername("john_doe")).thenReturn(true);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    @DisplayName("POST /register returns 400 when email already exists")
    void register_existingEmail_returns400() throws Exception {
        User req = new User();
        req.setUsername("new_user");
        req.setEmail("john@example.com");
        req.setPassword("pass1234");

        when(userService.existsByUsername("new_user")).thenReturn(false);
        when(userService.existsByEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    // ── GET /{id} ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id} returns 200 with profile when user exists")
    void getProfile_existingUser_returns200() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(john));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("GET /{id} returns 404 when user not found")
    void getProfile_unknownUser_returns404() throws Exception {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /{id} ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /{id} returns 200 with updated profile on valid request")
    void updateProfile_validRequest_returns200() throws Exception {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("jane_doe");
        req.setEmail("jane@example.com");

        User updated = new User();
        updated.setId(1L);
        updated.setUsername("jane_doe");
        updated.setEmail("jane@example.com");
        updated.setRole("USER");

        when(userService.updateProfile(eq(1L), eq("jane_doe"), eq("jane@example.com"))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane_doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    @DisplayName("PUT /{id} returns 400 when username is blank")
    void updateProfile_blankUsername_returns400() throws Exception {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("");
        req.setEmail("jane@example.com");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /{id} returns 400 when service throws for duplicate username")
    void updateProfile_duplicateUsername_returns400() throws Exception {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("taken_user");
        req.setEmail("jane@example.com");

        when(userService.updateProfile(eq(1L), eq("taken_user"), eq("jane@example.com")))
                .thenThrow(new IllegalArgumentException("Username already taken"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }

    // ── PUT /{id}/password ─────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /{id}/password returns 200 when current password matches")
    void changePassword_validRequest_returns200() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("pass1234");
        req.setNewPassword("newPass99");

        // void method — default Mockito behaviour is do-nothing (success)

        mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /{id}/password returns 400 when current password is wrong")
    void changePassword_wrongPassword_returns400() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrongPass");
        req.setNewPassword("newPass99");

        doThrow(new IllegalArgumentException("Current password is incorrect"))
                .when(userService).changePassword(eq(1L), eq("wrongPass"), eq("newPass99"));

        mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }

    @Test
    @DisplayName("PUT /{id}/password returns 400 when new password is too short")
    void changePassword_shortNewPassword_returns400() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("pass1234");
        req.setNewPassword("abc");       // less than 6 chars — fails @Size

        mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
