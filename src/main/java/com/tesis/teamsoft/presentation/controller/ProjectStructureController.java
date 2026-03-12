package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ProjectStructureDTO;
import com.tesis.teamsoft.service.implementation.ProjectStructureServiceImpl;
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
@Tag(name = "ProjectStructure")
@RequestMapping("/project-structure")
public class ProjectStructureController {

    private final ProjectStructureServiceImpl projectStructureService;

    @PostMapping()
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectStructureDTO.ProjectStructureResponseDTO> createProjectStructure(
            @Valid @RequestBody ProjectStructureDTO.ProjectStructureCreateDTO projectStructureDTO) {
        return new ResponseEntity<>(projectStructureService.saveProjectStructure(projectStructureDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectStructureDTO.ProjectStructureResponseDTO> updateProjectStructure(
            @Valid @RequestBody ProjectStructureDTO.ProjectStructureCreateDTO projectStructureDTO,
            @PathVariable Long id) {
        return new ResponseEntity<>(projectStructureService.updateProjectStructure(projectStructureDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<String> deleteProjectStructure(@PathVariable Long id) {
        return new ResponseEntity<>(projectStructureService.deleteProjectStructure(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<ProjectStructureDTO.ProjectStructureSimpleDTO>> findAllProjectStructure() {
        return new ResponseEntity<>(projectStructureService.findAllProjectStructure(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectStructureDTO.ProjectStructureResponseDTO> findProjectStructureById(@PathVariable Long id) {
        return new ResponseEntity<>(projectStructureService.findProjectStructureById(id), HttpStatus.OK);
    }
}