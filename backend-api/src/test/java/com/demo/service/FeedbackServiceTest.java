package com.demo.service;

import com.demo.entity.Feedback;
import com.demo.repository.FeedbackRepository;
import com.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackService Unit Tests")
class FeedbackServiceTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private FeedbackService service;

    private Feedback feedback;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setName("John Doe");
        feedback.setEmail("john@example.com");
        feedback.setCategory("FEEDBACK");
        feedback.setMessage("Great service!");
    }

    @Test
    @DisplayName("submit sets createdAt and status OPEN then saves")
    void submit_setsDefaults() {
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Feedback result = service.submit(feedback);

        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getCreatedAt()).isNotNull();
        verify(feedbackRepository).save(feedback);
    }

    @Test
    @DisplayName("submit preserves existing status if already set")
    void submit_preservesExistingStatus() {
        feedback.setStatus("CLOSED");
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Feedback result = service.submit(feedback);

        assertThat(result.getStatus()).isEqualTo("CLOSED");
    }

    @Test
    @DisplayName("getAll returns ordered feedback list")
    void getAll_returnsOrderedList() {
        when(feedbackRepository.findAllOrderByCreatedAtDesc()).thenReturn(List.of(feedback));

        List<Feedback> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("updateStatus updates feedback status and returns true")
    void updateStatus_found_updatesAndReturnsTrue() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        boolean result = service.updateStatus(1L, "IN_PROGRESS");

        assertThat(result).isTrue();
        assertThat(feedback.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("updateStatus returns false when feedback not found")
    void updateStatus_notFound_returnsFalse() {
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.updateStatus(99L, "CLOSED")).isFalse();
    }

    @Test
    @DisplayName("delete removes feedback and returns true")
    void delete_exists_returnsTrue() {
        when(feedbackRepository.existsById(1L)).thenReturn(true);

        assertThat(service.delete(1L)).isTrue();
        verify(feedbackRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete returns false when feedback not found")
    void delete_notFound_returnsFalse() {
        when(feedbackRepository.existsById(99L)).thenReturn(false);

        assertThat(service.delete(99L)).isFalse();
        verify(feedbackRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAdminStats returns all expected keys")
    void getAdminStats_returnsAllKeys() {
        when(feedbackRepository.countUsers()).thenReturn(10);
        when(feedbackRepository.countFamilyMembers()).thenReturn(30);
        when(feedbackRepository.countPets()).thenReturn(23);
        when(feedbackRepository.countFeedback()).thenReturn(18);

        Map<String, Object> stats = service.getAdminStats();

        assertThat(stats).containsKeys(
                "totalUsers", "totalFamilyMembers", "totalPets", "totalFeedback"
        );
        assertThat(stats.get("totalUsers")).isEqualTo(10);
        assertThat(stats.get("totalFamilyMembers")).isEqualTo(30);
        assertThat(stats.get("totalPets")).isEqualTo(23);
        assertThat(stats.get("totalFeedback")).isEqualTo(18);
    }
}
