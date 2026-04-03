package com.demo.service;

import com.demo.entity.Pet;
import com.demo.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public List<Pet> getByUserId(Long userId) {
        log.debug("Fetching all pets for userId={}", userId);
        return repository.findByUserId(userId);
    }

    /** Search + filter pets for a user. Pass empty strings to skip a filter. */
    public List<Pet> search(Long userId, String search, String species) {
        String like    = (search == null || search.isBlank()) ? "" : "%" + search.trim() + "%";
        String specStr = (species == null) ? "" : species.trim();
        log.debug("Searching pets userId={} search='{}' species='{}'", userId, like, specStr);
        return repository.search(userId, like, specStr);
    }

    public Pet add(Long userId, Pet pet) {
        log.info("Adding pet '{}' ({}) for userId={}", pet.getName(), pet.getSpecies(), userId);
        pet.setUserId(userId);
        Pet saved = repository.save(pet);
        log.info("Pet saved with id={}", saved.getId());
        return saved;
    }

    public Optional<Pet> update(Long userId, Long petId, Pet updated) {
        log.info("Updating pet id={} for userId={}", petId, userId);
        return repository.findById(petId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                log.warn("User {} attempted to update pet {} belonging to another user", userId, petId);
                throw new IllegalArgumentException("Pet does not belong to this user");
            }
            existing.setName(updated.getName());
            existing.setSpecies(updated.getSpecies());
            existing.setBreed(updated.getBreed());
            existing.setDateOfBirth(updated.getDateOfBirth());
            existing.setGender(updated.getGender());
            Pet saved = repository.save(existing);
            log.info("Pet id={} updated successfully", petId);
            return saved;
        });
    }

    public boolean delete(Long userId, Long petId) {
        log.info("Deleting pet id={} for userId={}", petId, userId);
        return repository.findById(petId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                log.warn("User {} attempted to delete pet {} belonging to another user", userId, petId);
                throw new IllegalArgumentException("Pet does not belong to this user");
            }
            repository.deleteById(petId);
            log.info("Pet id={} deleted", petId);
            return true;
        }).orElseGet(() -> {
            log.warn("Pet id={} not found", petId);
            return false;
        });
    }
}
