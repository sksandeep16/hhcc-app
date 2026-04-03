package com.demo.service;

import com.demo.dto.PageResponse;
import com.demo.entity.User;
import com.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String password) {
        log.info("Login attempt for username='{}'", username);
        Optional<User> user = userRepository.findByUsername(username)
                .filter(u -> password.equals(u.getPassword()));
        if (user.isPresent()) log.info("Login successful for username='{}'", username);
        else                  log.warn("Login failed for username='{}'", username);
        return user;
    }

    public boolean existsByUsername(String username) {
        log.debug("Checking existence for username='{}'", username);
        return userRepository.countByUsername(username) > 0;
    }

    public boolean existsByEmail(String email) {
        log.debug("Checking existence for email='{}'", email);
        return userRepository.countByEmail(email) > 0;
    }

    public User register(User user) {
        log.info("Registering new user username='{}'", user.getUsername());
        User saved = userRepository.save(user);
        log.info("User registered with id={}", saved.getId());
        return saved;
    }

    /** Returns all users — kept for backward compatibility. */
    public List<User> findAll() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        log.debug("Fetching user by id={}", id);
        return userRepository.findById(id);
    }

    public User updateProfile(Long id, String username, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getUsername().equalsIgnoreCase(username) && existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (!user.getEmail().equalsIgnoreCase(email) && existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setUsername(username);
        user.setEmail(email);
        User saved = userRepository.save(user);
        log.info("Profile updated for id={}", id);
        return saved;
    }

    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Password changed for id={}", id);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User id={} deleted by admin", id);
            return true;
        }
        log.warn("Delete failed — user id={} not found", id);
        return false;
    }

    public User adminUpdateUser(Long id, String username, String email, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getUsername().equalsIgnoreCase(username) && existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (!user.getEmail().equalsIgnoreCase(email) && existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.setUsername(username);
        user.setEmail(email);
        if (role != null && !role.isBlank()) user.setRole(role.toUpperCase());
        User saved = userRepository.save(user);
        log.info("Admin updated user id={}", id);
        return saved;
    }

    /** Paginated + searchable user list for admin. */
    public PageResponse<User> searchUsers(String search, String role, int page, int size) {
        String like    = (search == null || search.isBlank()) ? "" : "%" + search.trim() + "%";
        String roleStr = (role   == null) ? "" : role.trim();
        int offset     = page * size;
        log.debug("Admin searching users search='{}' role='{}' page={} size={}", like, roleStr, page, size);
        List<User> users = userRepository.searchUsers(like, roleStr, size, offset);
        long total       = userRepository.countSearchUsers(like, roleStr);
        return new PageResponse<>(users, page, size, total);
    }
}
