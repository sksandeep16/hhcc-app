package com.demo.repository;

import com.demo.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // ── Admin stats counts ────────────────────────────────────────────────────
    @Query(value = "SELECT COUNT(*) FROM users",            nativeQuery = true) int countUsers();
    @Query(value = "SELECT COUNT(*) FROM family_members",   nativeQuery = true) int countFamilyMembers();
    @Query(value = "SELECT COUNT(*) FROM pets",             nativeQuery = true) int countPets();
    @Query(value = "SELECT COUNT(*) FROM feedback", nativeQuery = true) int countFeedback();

    @Query(value = "SELECT * FROM feedback ORDER BY created_at DESC", nativeQuery = true)
    List<Feedback> findAllOrderByCreatedAtDesc();

    // ── Admin search + pagination ─────────────────────────────────────────────

    @Query(value = """
            SELECT * FROM feedback
            WHERE (:search = '' OR LOWER(name) LIKE LOWER(:search) OR LOWER(message) LIKE LOWER(:search) OR LOWER(email) LIKE LOWER(:search))
              AND (:status = '' OR status = :status)
              AND (:category = '' OR category = :category)
            ORDER BY created_at DESC
            LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<Feedback> searchFeedback(@Param("search") String search,
                                  @Param("status") String status,
                                  @Param("category") String category,
                                  @Param("size") int size,
                                  @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(*) FROM feedback
            WHERE (:search = '' OR LOWER(name) LIKE LOWER(:search) OR LOWER(message) LIKE LOWER(:search) OR LOWER(email) LIKE LOWER(:search))
              AND (:status = '' OR status = :status)
              AND (:category = '' OR category = :category)
            """, nativeQuery = true)
    long countSearchFeedback(@Param("search") String search,
                             @Param("status") String status,
                             @Param("category") String category);
}
