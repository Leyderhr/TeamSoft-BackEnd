package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.CompetenceImportanceDTO;
import com.tesis.teamsoft.service.implementation.CompetenceImportanceServiceImpl;
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
@Tag(name = "CompetenceImportance")
@RequestMapping("/competenceImportance")
public class CompetenceImportanceController {


    private final CompetenceImportanceServiceImpl competenceImportanceService;


    @PostMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> createCompetenceImportance(
            @Valid @RequestBody CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO) {
        return new ResponseEntity<>(competenceImportanceService.saveCompetenceImportance(competenceImportanceDTO),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> updateCompetenceImportance(
            @Valid @RequestBody CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO,
            @PathVariable Long id) {
        return new ResponseEntity<>(competenceImportanceService.updateCompetenceImportance(competenceImportanceDTO, id),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteCompetenceImportance(@PathVariable Long id) {
        return new ResponseEntity<>(competenceImportanceService.deleteCompetenceImportance(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<CompetenceImportanceDTO.CompetenceImportanceResponseDTO>> findAllCompetenceImportance() {
        return new ResponseEntity<>(competenceImportanceService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> findCompetenceImportanceById(@PathVariable Long id) {
        return new ResponseEntity<>(competenceImportanceService.findCompetenceImportanceById(id), HttpStatus.OK);
    }
}