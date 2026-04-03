package com.demo.repository;

import com.demo.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    @Query(value = "SELECT * FROM family_members WHERE user_id = :userId", nativeQuery = true)
    List<FamilyMember> findByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT * FROM family_members
            WHERE user_id = :userId
              AND (:search = '' OR LOWER(first_name) LIKE LOWER(:search) OR LOWER(last_name) LIKE LOWER(:search))
              AND (:relationship = '' OR relationship = :relationship)
            """, nativeQuery = true)
    List<FamilyMember> search(@Param("userId") Long userId,
                              @Param("search") String search,
                              @Param("relationship") String relationship);
}
