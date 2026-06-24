package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.FilterDTO;
import com.tesis.teamsoft.presentation.dto.PersonDTO;
import com.tesis.teamsoft.service.implementation.FilterServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterServiceImpl filterService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_RRHH') OR hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<PersonDTO.PersonResponseDTO>> filterPersons(@RequestBody FilterDTO.FilterRequestDTO filterRequest) {
        List<PersonDTO.PersonResponseDTO> result = filterService.filterPersons(filterRequest);
        return ResponseEntity.ok(result);
    }
}