package com.demo.service;

import com.demo.dto.PageResponse;
import com.demo.entity.Feedback;
import com.demo.repository.FeedbackRepository;
import com.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class FeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);

    private final FeedbackRepository feedbackRepository;
    private final UserRepository     userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository     = userRepository;
    }

    public Feedback submit(Feedback feedback) {
        log.info("Submitting feedback from name='{}' category='{}'", feedback.getName(), feedback.getCategory());
        feedback.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        if (feedback.getStatus() == null || feedback.getStatus().isBlank()) {
            feedback.setStatus("OPEN");
        }
        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback saved with id={}", saved.getId());
        return saved;
    }

    /** All feedback ordered newest first — kept for backward compatibility. */
    public List<Feedback> getAll() {
        log.debug("Fetching all feedback entries");
        return feedbackRepository.findAllOrderByCreatedAtDesc();
    }

    /** Paginated + searchable feedback list for admin. */
    public PageResponse<Feedback> search(String search, String status, String category, int page, int size) {
        String like  = (search   == null || search.isBlank())   ? "" : "%" + search.trim()   + "%";
        String st    = (status   == null) ? "" : status.trim();
        String cat   = (category == null) ? "" : category.trim();
        int    offset = page * size;
        log.debug("Admin searching feedback search='{}' status='{}' category='{}' page={}", like, st, cat, page);
        List<Feedback> items = feedbackRepository.searchFeedback(like, st, cat, size, offset);
        long total           = feedbackRepository.countSearchFeedback(like, st, cat);
        return new PageResponse<>(items, page, size, total);
    }

    public boolean updateStatus(Long id, String status) {
        log.info("Updating feedback id={} to status='{}'", id, status);
        return feedbackRepository.findById(id).map(f -> {
            f.setStatus(status);
            feedbackRepository.save(f);
            log.info("Feedback id={} status updated to '{}'", id, status);
            return true;
        }).orElseGet(() -> {
            log.warn("Feedback id={} not found for status update", id);
            return false;
        });
    }

    public boolean delete(Long id) {
        log.info("Deleting feedback id={}", id);
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
            log.info("Feedback id={} deleted", id);
            return true;
        }
        log.warn("Feedback id={} not found for deletion", id);
        return false;
    }

    public Map<String, Object> getAdminStats() {
        log.debug("Generating admin stats");
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",         feedbackRepository.countUsers());
        stats.put("totalFamilyMembers", feedbackRepository.countFamilyMembers());
        stats.put("totalPets",          feedbackRepository.countPets());
        stats.put("totalFeedback",      feedbackRepository.countFeedback());
        log.info("Admin stats generated: {}", stats);
        return stats;
    }
}
