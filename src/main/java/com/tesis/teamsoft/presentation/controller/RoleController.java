package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.RoleDTO;
import com.tesis.teamsoft.service.implementation.RoleServiceImpl;
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
@Tag(name = "Role")
@RequestMapping("/role")
public class RoleController {

    private final RoleServiceImpl roleService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleDTO.RoleResponseDTO> createRole(@Valid @RequestBody RoleDTO.RoleCreateDTO roleDTO) {
        return new ResponseEntity<>(roleService.saveRole(roleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleDTO.RoleResponseDTO> updateRole(@Valid @RequestBody RoleDTO.RoleCreateDTO roleDTO,
                                                              @PathVariable Long id) {
        return new ResponseEntity<>(roleService.updateRole(roleDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        return new ResponseEntity<>(roleService.deleteRole(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<RoleDTO.RoleResponseDTO>> findAllRole() {
        return new ResponseEntity<>(roleService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleDTO.RoleResponseDTO> findRoleById(@PathVariable Long id) {
        return new ResponseEntity<>(roleService.findRoleById(id), HttpStatus.OK);
    }
}