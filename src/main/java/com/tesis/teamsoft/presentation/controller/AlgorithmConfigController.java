package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.AlgorithmConfigDTO;
import com.tesis.teamsoft.service.implementation.AlgorithmConfigServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config/algorithm")
@RequiredArgsConstructor
public class AlgorithmConfigController {

    private final AlgorithmConfigServiceImpl algorithmConfigService;

    @PutMapping()
    @PreAuthorize("hasRole('EXPERIMENTADOR')")
    public ResponseEntity<String> updateConfig(@Valid @RequestBody AlgorithmConfigDTO.AlgorithmConfigUpdateDTO configDTO) {
        algorithmConfigService.saveAlgorithmConfig(configDTO);
        return ResponseEntity.ok("Algorithm configuration updated successfully");
    }
}
