package com.demo.service;

import com.demo.entity.FamilyMember;
import com.demo.repository.FamilyMemberRepository;
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
@DisplayName("FamilyMemberService Unit Tests")
class FamilyMemberServiceTest {

    @Mock private FamilyMemberRepository repository;
    @InjectMocks private FamilyMemberService service;

    private FamilyMember member;

    @BeforeEach
    void setUp() {
        member = new FamilyMember();
        member.setId(1L);
        member.setUserId(10L);
        member.setFirstName("Mary");
        member.setLastName("Doe");
        member.setRelationship("Spouse");
    }

    @Test
    @DisplayName("getByUserId returns members for given user")
    void getByUserId_returnsList() {
        when(repository.findByUserId(10L)).thenReturn(List.of(member));

        List<FamilyMember> result = service.getByUserId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Mary");
    }

    @Test
    @DisplayName("add sets userId and saves member")
    void add_setsUserIdAndSaves() {
        FamilyMember input = new FamilyMember();
        input.setFirstName("Tom");
        input.setLastName("Doe");
        input.setRelationship("Child");

        when(repository.save(any())).thenAnswer(i -> {
            FamilyMember m = i.getArgument(0);
            m.setId(2L);
            return m;
        });

        FamilyMember result = service.add(10L, input);

        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getId()).isEqualTo(2L);
        verify(repository).save(input);
    }

    @Test
    @DisplayName("update modifies existing member successfully")
    void update_existingMember_updatesFields() {
        FamilyMember updated = new FamilyMember();
        updated.setFirstName("Mary Updated");
        updated.setLastName("Doe");
        updated.setRelationship("Spouse");

        when(repository.findById(1L)).thenReturn(Optional.of(member));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<FamilyMember> result = service.update(10L, 1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Mary Updated");
    }

    @Test
    @DisplayName("update throws exception when member belongs to different user")
    void update_wrongUser_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.update(99L, 1L, member))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    @DisplayName("delete removes member and returns true")
    void delete_existingMember_returnsTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        boolean result = service.delete(10L, 1L);

        assertThat(result).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete returns false when member not found")
    void delete_notFound_returnsFalse() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        boolean result = service.delete(10L, 99L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete throws exception when member belongs to different user")
    void delete_wrongUser_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.delete(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
