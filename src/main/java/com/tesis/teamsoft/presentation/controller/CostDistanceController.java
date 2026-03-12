package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.CostDistanceDTO;
import com.tesis.teamsoft.service.implementation.CostDistanceServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CostDistance")
@RequestMapping("/costDistance")
public class CostDistanceController {

    private final CostDistanceServiceImpl costDistanceService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CostDistanceDTO.CostDistanceResponseDTO> createCostDistance(@Valid @RequestBody CostDistanceDTO.CostDistanceCreateDTO costDistanceDTO) {
        return new ResponseEntity<>(costDistanceService.saveCostDistance(costDistanceDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CostDistanceDTO.CostDistanceResponseDTO> updateCostDistance(@Valid @RequestBody CostDistanceDTO.CostDistanceCreateDTO costDistanceDTO, @PathVariable Long id) {
        return new ResponseEntity<>(costDistanceService.updateCostDistance(costDistanceDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteCostDistance(@PathVariable Long id) {
        return new ResponseEntity<>(costDistanceService.deleteCostDistance(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<CostDistanceDTO.CostDistanceResponseDTO>> findAllCostDistance() {
        return new ResponseEntity<>(costDistanceService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CostDistanceDTO.CostDistanceResponseDTO> findCostDistanceById(@PathVariable Long id) {
        return new ResponseEntity<>(costDistanceService.findCostDistanceById(id), HttpStatus.OK);
    }
}