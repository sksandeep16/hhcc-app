package com.demo.service;

import com.demo.entity.CareService;
import com.demo.repository.CareServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareServiceService {

    private static final Logger log = LoggerFactory.getLogger(CareServiceService.class);

    private final CareServiceRepository repository;

    public CareServiceService(CareServiceRepository repository) {
        this.repository = repository;
    }

    public List<CareService> getAll() {
        log.debug("Fetching all care services");
        return repository.findAllOrderedBySortOrder();
    }

    public Optional<CareService> getById(Long id) {
        log.debug("Fetching care service id={}", id);
        return repository.findById(id);
    }
}
