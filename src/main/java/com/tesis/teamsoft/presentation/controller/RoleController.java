package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import com.tesis.teamsoft.presentation.dto.RoleDTO;
import com.tesis.teamsoft.service.implementation.RoleServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ImportResultDTO> importRoles(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean updateIfExist) {
        try {
            ImportResultDTO result = roleService.importRoles(file.getInputStream(), updateIfExist);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }

    @GetMapping(value = "/export", produces = "text/csv")
//    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<byte[]> exportRoles() {
        InputStream is = roleService.exportRoles();
        byte[] bytes;
        try {
            bytes = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=roles.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}