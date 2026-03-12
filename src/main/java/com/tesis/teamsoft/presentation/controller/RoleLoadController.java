package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.RoleLoadDTO;
import com.tesis.teamsoft.service.implementation.RoleLoadServiceImpl;
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
@Tag(name = "RoleLoad")
@RequestMapping("/roleLoad")
public class RoleLoadController {

    private final RoleLoadServiceImpl roleLoadService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleLoadDTO.RoleLoadResponseDTO> createRoleLoad(@Valid @RequestBody RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO) {
        return new ResponseEntity<>(roleLoadService.saveRoleLoad(roleLoadDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleLoadDTO.RoleLoadResponseDTO> updateRoleLoad(@Valid @RequestBody RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO,
                                                                          @PathVariable Long id) {
        return new ResponseEntity<>(roleLoadService.updateRoleLoad(roleLoadDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteRoleLoad(@PathVariable Long id) {
        return new ResponseEntity<>(roleLoadService.deleteRoleLoad(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<RoleLoadDTO.RoleLoadResponseDTO>> findAllRoleLoad() {
        return new ResponseEntity<>(roleLoadService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RoleLoadDTO.RoleLoadResponseDTO> findRoleLoadById(@PathVariable Long id) {
        return new ResponseEntity<>(roleLoadService.findRoleLoadById(id), HttpStatus.OK);
    }
}