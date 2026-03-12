package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ProjectDTO;
import com.tesis.teamsoft.service.implementation.ProjectServiceImpl;
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
@RequestMapping("/project")
@Tag(name = "Project")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    @PostMapping()
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<ProjectDTO.ProjectResponseDTO>> createProjects(@Valid @RequestBody List<ProjectDTO.ProjectCreateDTO> projectDTOs) {
        return new ResponseEntity<>(projectService.saveProjects(projectDTOs), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectDTO.ProjectResponseDTO> updateProject(
            @PathVariable Long id, @Valid @RequestBody ProjectDTO.ProjectCreateDTO projectDTO) {
        return new ResponseEntity<>(projectService.updateProject(projectDTO, id), HttpStatus.CREATED);
    }

    @PutMapping("close/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectDTO.ProjectResponseDTO> closeProject(@PathVariable Long id){
        return new ResponseEntity<>(projectService.closeProject(id), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.deleteProject(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<ProjectDTO.ProjectSimpleDTO>> findAllProjects() {
        return new ResponseEntity<>(projectService.findAllProjects(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectDTO.ProjectResponseDTO> findProjectById(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.findProjectById(id), HttpStatus.OK);
    }
}