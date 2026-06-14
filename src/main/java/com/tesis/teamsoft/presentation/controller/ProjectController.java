package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import com.tesis.teamsoft.presentation.dto.AssignedRoleDTO;
import com.tesis.teamsoft.presentation.dto.CloseProjectDTO;
import com.tesis.teamsoft.presentation.dto.ProjectDTO;
import com.tesis.teamsoft.presentation.dto.RolePersonEvaluationDTO;
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
    @PreAuthorize("hasRole('JEFE_DE_EQUIPO') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectDTO.ProjectResponseDTO> closeProject(
            @PathVariable Long id, @Valid @RequestBody CloseProjectDTO dto){
        return new ResponseEntity<>(projectService.closeProject(id, dto), HttpStatus.OK);
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

    @GetMapping("/boss-competitions")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<ProjectDTO.ProjectBossCompetitionsDTO>> getBossCompetitionsByIds(
            @RequestParam List<Long> ids) {
        return ResponseEntity.ok(projectService.findBossRoleCompetitionsByProjectIds(ids));
    }

    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO') OR hasRole('GESTOR_RRHH') OR hasRole('JEFE_DE_EQUIPO')")
    public ResponseEntity<List<ProjectDTO.ProjectSimpleDTO>> findProjectsByState(@PathVariable ProjectState state) {
        return new ResponseEntity<>(projectService.findAllProjectsByState(state), HttpStatus.OK);
    }

    @GetMapping("/non-boss-roles")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<ProjectDTO.ProjectNonBossRolesDTO>> getNonBossRolesByProjectIds(
            @RequestParam List<Long> ids) {
        return ResponseEntity.ok(projectService.findNonBossRolesByProjectIds(ids));
    }

    @GetMapping("/{id}/non-boss-assigned-roles")
    @PreAuthorize("hasRole('JEFE_DE_EQUIPO') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<List<AssignedRoleDTO>> getNonBossAssignedRoles(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.findNonBossAssignedRoles(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/boss-assigned-role")
    @PreAuthorize("hasRole('JEFE_DE_EQUIPO') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<AssignedRoleDTO> getBossAssignedRole(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.findBossAssignedRole(id), HttpStatus.OK);
    }

    @PutMapping("finalize/{id}")
    @PreAuthorize("hasRole('JEFE_DE_EQUIPO') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<ProjectDTO.ProjectResponseDTO> finalizeProject(
            @PathVariable Long id, @Valid @RequestBody List<RolePersonEvaluationDTO> evaluations) {
        return new ResponseEntity<>(projectService.finalizeProject(id, evaluations), HttpStatus.OK);
    }
}