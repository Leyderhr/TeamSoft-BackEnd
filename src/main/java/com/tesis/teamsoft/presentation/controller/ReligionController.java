package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ReligionDTO;
import com.tesis.teamsoft.service.implementation.ReligionServiceImpl;
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
@Tag(name = "Religions")
@RequestMapping("/religion")
public class ReligionController {

    private final ReligionServiceImpl religionService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ReligionDTO.ReligionResponseDTO> createReligion(@Valid @RequestBody ReligionDTO.ReligionCreateDTO religionDTO) {
        return new ResponseEntity<>(religionService.saveReligion(religionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ReligionDTO.ReligionResponseDTO> updateReligion(@Valid @RequestBody ReligionDTO.ReligionCreateDTO religionDTO, @PathVariable Long id) {
        return new ResponseEntity<>(religionService.updateReligion(religionDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteReligion(@PathVariable Long id) {
        return new ResponseEntity<>(religionService.deleteReligion(id), HttpStatus.NO_CONTENT);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<ReligionDTO.ReligionResponseDTO>> findAllReligions() {
        return new ResponseEntity<>(religionService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ReligionDTO.ReligionResponseDTO> findUserById(@PathVariable Long id) {
        return new ResponseEntity<>(religionService.findReligionById(id), HttpStatus.OK);
    }
}
