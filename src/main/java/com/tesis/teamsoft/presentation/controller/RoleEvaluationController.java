package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.RoleEvaluationDTO;
import com.tesis.teamsoft.service.implementation.RoleEvaluationServiceImpl;
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
@Tag(name = "RoleEvaluation")
@RequestMapping("/roleEvaluation")
public class RoleEvaluationController {

    private final RoleEvaluationServiceImpl roleEvaluationService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleEvaluationDTO.RoleEvaluationResponseDTO> createRoleEvaluation(@Valid @RequestBody RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO) {
        return new ResponseEntity<>(roleEvaluationService.saveRoleEvaluation(roleEvaluationDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleEvaluationDTO.RoleEvaluationResponseDTO> updateRoleEvaluation(@Valid @RequestBody RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO,
                                                                                            @PathVariable Long id) {
        return new ResponseEntity<>(roleEvaluationService.updateRoleEvaluation(roleEvaluationDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteRoleEvaluation(@PathVariable Long id) {
        return new ResponseEntity<>(roleEvaluationService.deleteRoleEvaluation(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<RoleEvaluationDTO.RoleEvaluationResponseDTO>> findAllRoleEvaluation() {
        return new ResponseEntity<>(roleEvaluationService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleEvaluationDTO.RoleEvaluationResponseDTO> findRoleEvaluationById(@PathVariable Long id) {
        return new ResponseEntity<>(roleEvaluationService.findRoleEvaluationById(id), HttpStatus.OK);
    }
}