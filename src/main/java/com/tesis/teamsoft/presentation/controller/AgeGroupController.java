package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.service.implementation.AgeGroupServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "AgeGroups")
@RequestMapping("/ageGroups")
public class AgeGroupController {


    private final AgeGroupServiceImpl ageGroupService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<AgeGroupDTO.AgeGroupResponseDTO> createAgeGroup(@Valid @RequestBody AgeGroupDTO.AgeGroupCreateDTO dto) {
        AgeGroupDTO.AgeGroupResponseDTO saved = ageGroupService.saveAgeGroup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<AgeGroupDTO.AgeGroupResponseDTO> updateAgeGroup(@Valid @RequestBody AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO, @PathVariable Long id) {
        return new ResponseEntity<>(ageGroupService.updateAgeGroup(ageGroupDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteAgeGroup(@PathVariable Long id) {
        return new ResponseEntity<>(ageGroupService.deleteAgeGroup(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<AgeGroupDTO.AgeGroupResponseDTO>> findAllAgeGroup() {
        List<AgeGroupDTO.AgeGroupResponseDTO> list = ageGroupService.findAllByOrderByIdAsc();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<AgeGroupDTO.AgeGroupResponseDTO> findAgeGroupById(@PathVariable Long id) {
        AgeGroupDTO.AgeGroupResponseDTO dto = ageGroupService.findAgeGroupById(id);
        return ResponseEntity.ok(dto);
    }
}