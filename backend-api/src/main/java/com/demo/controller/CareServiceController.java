package com.demo.controller;

import com.demo.entity.CareService;
import com.demo.service.CareServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Services", description = "CareApp service catalogue")
@RestController
@RequestMapping("/api/services")
public class CareServiceController {

    private final CareServiceService careServiceService;

    public CareServiceController(CareServiceService careServiceService) {
        this.careServiceService = careServiceService;
    }

    @Operation(summary = "List all care services", description = "Returns all services ordered by sort_order")
    @GetMapping
    public ResponseEntity<List<CareService>> getAll() {
        return ResponseEntity.ok(careServiceService.getAll());
    }

    @Operation(summary = "Get a single care service by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CareService> getById(@PathVariable Long id) {
        return careServiceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
