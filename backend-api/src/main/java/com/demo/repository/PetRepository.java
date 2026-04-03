package com.demo.repository;

import com.demo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query(value = "SELECT * FROM pets WHERE user_id = :userId", nativeQuery = true)
    List<Pet> findByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT * FROM pets
            WHERE user_id = :userId
              AND (:search = '' OR LOWER(name) LIKE LOWER(:search))
              AND (:species = '' OR species = :species)
            """, nativeQuery = true)
    List<Pet> search(@Param("userId") Long userId,
                     @Param("search") String search,
                     @Param("species") String species);
}
