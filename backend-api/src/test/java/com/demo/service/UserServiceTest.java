package com.demo.service;

import com.demo.entity.User;
import com.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("pass1234");
        testUser.setRole("USER");
    }

    // ── login ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login returns user when credentials are valid")
    void login_validCredentials_returnsUser() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.login("john_doe", "pass1234");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john_doe");
        verify(userRepository).findByUsername("john_doe");
    }

    @Test
    @DisplayName("login returns empty when password is wrong")
    void login_wrongPassword_returnsEmpty() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.login("john_doe", "wrongpassword");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("login returns empty when username does not exist")
    void login_unknownUsername_returnsEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.login("unknown", "pass1234");

        assertThat(result).isEmpty();
    }

    // ── exists checks ──────────────────────────────────────────────────────

    @Test
    @DisplayName("existsByUsername returns true when username exists")
    void existsByUsername_exists_returnsTrue() {
        when(userRepository.countByUsername("john_doe")).thenReturn(1);

        assertThat(userService.existsByUsername("john_doe")).isTrue();
    }

    @Test
    @DisplayName("existsByUsername returns false when username does not exist")
    void existsByUsername_notExists_returnsFalse() {
        when(userRepository.countByUsername("new_user")).thenReturn(0);

        assertThat(userService.existsByUsername("new_user")).isFalse();
    }

    @Test
    @DisplayName("existsByEmail returns true when email exists")
    void existsByEmail_exists_returnsTrue() {
        when(userRepository.countByEmail("john@example.com")).thenReturn(1);

        assertThat(userService.existsByEmail("john@example.com")).isTrue();
    }

    // ── register ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("register saves and returns the user")
    void register_validUser_savesAndReturns() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(testUser);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("john_doe");
        verify(userRepository).save(testUser);
    }

    // ── findAll ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll returns list of all users")
    void findAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
    }

    // ── findById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById returns user when found")
    void findById_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john_doe");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("findById returns empty when user not found")
    void findById_unknownUser_returnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertThat(result).isEmpty();
    }

    // ── updateProfile ──────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile updates username and email when no conflicts")
    void updateProfile_success_updatesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // "jane_doe" != "john_doe" → uniqueness check fires → returns 0 (available)
        when(userRepository.countByUsername("jane_doe")).thenReturn(0);
        // "jane@example.com" != "john@example.com" → uniqueness check fires → returns 0
        when(userRepository.countByEmail("jane@example.com")).thenReturn(0);

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("jane_doe");
        saved.setEmail("jane@example.com");
        saved.setRole("USER");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.updateProfile(1L, "jane_doe", "jane@example.com");

        assertThat(result.getUsername()).isEqualTo("jane_doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("jane_doe") && u.getEmail().equals("jane@example.com")));
    }

    @Test
    @DisplayName("updateProfile keeps same username without checking uniqueness")
    void updateProfile_sameUsername_skipsUsernameCheck() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // Same username (case-insensitive) → short-circuits → countByUsername NOT called
        when(userRepository.countByEmail("new@example.com")).thenReturn(0);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile(1L, "john_doe", "new@example.com");

        verify(userRepository, never()).countByUsername("john_doe");
    }

    @Test
    @DisplayName("updateProfile throws IllegalArgumentException when new username is taken")
    void updateProfile_duplicateUsername_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.countByUsername("taken_user")).thenReturn(1);

        assertThatThrownBy(() -> userService.updateProfile(1L, "taken_user", "john@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    @DisplayName("updateProfile throws IllegalArgumentException when new email is taken")
    void updateProfile_duplicateEmail_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // Same username → no username check
        when(userRepository.countByEmail("taken@example.com")).thenReturn(1);

        assertThatThrownBy(() -> userService.updateProfile(1L, "john_doe", "taken@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    @DisplayName("updateProfile throws IllegalArgumentException when user not found")
    void updateProfile_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(99L, "x", "x@x.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── changePassword ─────────────────────────────────────────────────────

    @Test
    @DisplayName("changePassword saves hashed new password when current matches")
    void changePassword_success_savesNewPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(1L, "pass1234", "newPass99");

        verify(userRepository).save(argThat(u -> u.getPassword().equals("newPass99")));
    }

    @Test
    @DisplayName("changePassword throws IllegalArgumentException when current password is wrong")
    void changePassword_wrongCurrentPassword_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.changePassword(1L, "wrongPass", "newPass99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    @DisplayName("changePassword throws IllegalArgumentException when user not found")
    void changePassword_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, "pass1234", "newPass99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
