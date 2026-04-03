package com.demo.controller;

import com.demo.dto.PageResponse;
import com.demo.entity.Feedback;
import com.demo.entity.User;
import com.demo.service.FeedbackService;
import com.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Feedback & Admin", description = "Feedback submission and admin management endpoints")
@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService     userService;

    public FeedbackController(FeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService     = userService;
    }

    @Operation(summary = "Submit feedback")
    @PostMapping("/feedback")
    public ResponseEntity<Feedback> submit(@Valid @RequestBody Feedback feedback) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.submit(feedback));
    }

    @Operation(summary = "Admin — list all feedback with search and pagination",
               description = "Search by name/email/message. Filter by status or category.")
    @GetMapping("/admin/feedback")
    public PageResponse<Feedback> getAll(
            @Parameter(description = "Search by name, email or message") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status: OPEN | IN_PROGRESS | CLOSED") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by category: FEEDBACK | SUPPORT") @RequestParam(required = false) String category,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size) {
        return feedbackService.search(search, status, category, page, size);
    }

    @Operation(summary = "Admin — update feedback status")
    @PatchMapping("/admin/feedback/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return feedbackService.updateStatus(id, status)
                ? ResponseEntity.ok("Status updated")
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Admin — delete feedback")
    @DeleteMapping("/admin/feedback/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return feedbackService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Admin — get dashboard stats")
    @GetMapping("/admin/stats")
    public Map<String, Object> getStats() {
        return feedbackService.getAdminStats();
    }

    @Operation(summary = "Admin — list all users with search and pagination")
    @GetMapping("/admin/users")
    public PageResponse<User> getAllUsers(
            @Parameter(description = "Search by username or email") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by role: USER | ADMIN") @RequestParam(required = false) String role,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size) {
        return userService.searchUsers(search, role, page, size);
    }

    @Operation(summary = "Admin — update a user (username, email, role)")
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> adminUpdateUser(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email    = body.get("email");
        String role     = body.get("role");
        User updated = userService.adminUpdateUser(id, username, email, role);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Admin — delete a user")
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> adminDeleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
