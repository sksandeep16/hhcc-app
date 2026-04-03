package com.demo.repository;

import com.demo.entity.CareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareServiceRepository extends JpaRepository<CareService, Long> {

    @Query("SELECT s FROM CareService s ORDER BY s.sortOrder ASC")
    List<CareService> findAllOrderedBySortOrder();
}
