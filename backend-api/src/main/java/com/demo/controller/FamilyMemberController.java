package com.demo.controller;

import com.demo.entity.FamilyMember;
import com.demo.service.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Family Members", description = "Manage family members for a user")
@RestController
@RequestMapping("/api/users/{userId}/family-members")
public class FamilyMemberController {

    private static final Logger log = LoggerFactory.getLogger(FamilyMemberController.class);

    private final FamilyMemberService service;

    public FamilyMemberController(FamilyMemberService service) {
        this.service = service;
    }

    @Operation(summary = "List family members", description = "Returns family members. Optionally filter by search term or relationship.")
    @GetMapping
    public List<FamilyMember> getAll(
            @PathVariable Long userId,
            @Parameter(description = "Search by first or last name") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by relationship (Spouse, Child, Parent …)") @RequestParam(required = false) String relationship) {
        log.debug("GET /api/users/{}/family-members search='{}' relationship='{}'", userId, search, relationship);
        if ((search == null || search.isBlank()) && (relationship == null || relationship.isBlank())) {
            return service.getByUserId(userId);
        }
        return service.search(userId, search, relationship);
    }

    @Operation(summary = "Add family member")
    @PostMapping
    public ResponseEntity<FamilyMember> add(@PathVariable Long userId,
                                            @Valid @RequestBody FamilyMember member) {
        log.info("POST /api/users/{}/family-members", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(userId, member));
    }

    @Operation(summary = "Update family member")
    @PutMapping("/{memberId}")
    public ResponseEntity<FamilyMember> update(@PathVariable Long userId,
                                               @PathVariable Long memberId,
                                               @Valid @RequestBody FamilyMember member) {
        log.info("PUT /api/users/{}/family-members/{}", userId, memberId);
        return service.update(userId, memberId, member)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Family member id={} not found for userId={}", memberId, userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete family member")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long memberId) {
        log.info("DELETE /api/users/{}/family-members/{}", userId, memberId);
        return service.delete(userId, memberId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
