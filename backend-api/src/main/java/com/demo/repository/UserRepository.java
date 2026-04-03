package com.demo.repository;

import com.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE username = :username LIMIT 1", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) FROM users WHERE username = :username", nativeQuery = true)
    int countByUsername(@Param("username") String username);

    @Query(value = "SELECT COUNT(*) FROM users WHERE email = :email", nativeQuery = true)
    int countByEmail(@Param("email") String email);

    // ── Admin search ──────────────────────────────────────────────────────────

    @Query(value = """
            SELECT * FROM users
            WHERE (:search = '' OR LOWER(username) LIKE LOWER(:search) OR LOWER(email) LIKE LOWER(:search))
              AND (:role = '' OR role = :role)
            ORDER BY id
            LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<User> searchUsers(@Param("search") String search,
                           @Param("role") String role,
                           @Param("size") int size,
                           @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(*) FROM users
            WHERE (:search = '' OR LOWER(username) LIKE LOWER(:search) OR LOWER(email) LIKE LOWER(:search))
              AND (:role = '' OR role = :role)
            """, nativeQuery = true)
    long countSearchUsers(@Param("search") String search,
                          @Param("role") String role);
}
