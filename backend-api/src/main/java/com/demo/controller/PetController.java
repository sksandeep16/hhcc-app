package com.demo.controller;

import com.demo.entity.Pet;
import com.demo.service.PetService;
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

@Tag(name = "Pets", description = "Manage pets for a user")
@RestController
@RequestMapping("/api/users/{userId}/pets")
public class PetController {

    private static final Logger log = LoggerFactory.getLogger(PetController.class);

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @Operation(summary = "List pets", description = "Returns pets. Optionally search by name or filter by species.")
    @GetMapping
    public List<Pet> getAll(
            @PathVariable Long userId,
            @Parameter(description = "Search by pet name") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by species (Dog, Cat, Bird …)") @RequestParam(required = false) String species) {
        log.debug("GET /api/users/{}/pets search='{}' species='{}'", userId, search, species);
        if ((search == null || search.isBlank()) && (species == null || species.isBlank())) {
            return service.getByUserId(userId);
        }
        return service.search(userId, search, species);
    }

    @Operation(summary = "Add pet")
    @PostMapping
    public ResponseEntity<Pet> add(@PathVariable Long userId,
                                   @Valid @RequestBody Pet pet) {
        log.info("POST /api/users/{}/pets name='{}'", userId, pet.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(userId, pet));
    }

    @Operation(summary = "Update pet")
    @PutMapping("/{petId}")
    public ResponseEntity<Pet> update(@PathVariable Long userId,
                                      @PathVariable Long petId,
                                      @Valid @RequestBody Pet pet) {
        log.info("PUT /api/users/{}/pets/{}", userId, petId);
        return service.update(userId, petId, pet)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Pet id={} not found for userId={}", petId, userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete pet")
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long petId) {
        log.info("DELETE /api/users/{}/pets/{}", userId, petId);
        return service.delete(userId, petId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
