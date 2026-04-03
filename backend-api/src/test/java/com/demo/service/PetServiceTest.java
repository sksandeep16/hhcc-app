package com.demo.service;

import com.demo.entity.Pet;
import com.demo.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Unit Tests")
class PetServiceTest {

    @Mock private PetRepository repository;
    @InjectMocks private PetService service;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setUserId(10L);
        pet.setName("Buddy");
        pet.setSpecies("Dog");
        pet.setBreed("Golden Retriever");
        pet.setGender("Male");
    }

    @Test
    @DisplayName("getByUserId returns pets for user")
    void getByUserId_returnsPets() {
        when(repository.findByUserId(10L)).thenReturn(List.of(pet));

        List<Pet> result = service.getByUserId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Buddy");
    }

    @Test
    @DisplayName("add sets userId and persists pet")
    void add_setsUserIdAndSaves() {
        Pet input = new Pet();
        input.setName("Whiskers");
        input.setSpecies("Cat");
        input.setGender("Female");

        when(repository.save(any())).thenAnswer(i -> {
            Pet p = i.getArgument(0);
            p.setId(2L);
            return p;
        });

        Pet result = service.add(10L, input);

        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("update modifies pet fields")
    void update_existingPet_updatesFields() {
        Pet updated = new Pet();
        updated.setName("Buddy Updated");
        updated.setSpecies("Dog");
        updated.setBreed("Labrador");
        updated.setGender("Male");

        when(repository.findById(1L)).thenReturn(Optional.of(pet));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Pet> result = service.update(10L, 1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Buddy Updated");
        assertThat(result.get().getBreed()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("update throws when pet belongs to different user")
    void update_wrongUser_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(pet));

        assertThatThrownBy(() -> service.update(99L, 1L, pet))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("delete removes pet and returns true")
    void delete_existingPet_returnsTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(pet));

        boolean result = service.delete(10L, 1L);

        assertThat(result).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete returns false for non-existent pet")
    void delete_notFound_returnsFalse() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        boolean result = service.delete(10L, 99L);

        assertThat(result).isFalse();
    }
}
