package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.LevelsDTO;
import com.tesis.teamsoft.service.implementation.LevelsServiceImpl;
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
@Tag(name = "Levels")
@RequestMapping("/levels")
public class LevelsController {

    private final LevelsServiceImpl levelsService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<LevelsDTO.LevelsResponseDTO> createLevels(@Valid @RequestBody LevelsDTO.LevelsCreateDTO levelsDTO) {
        return new ResponseEntity<>(levelsService.saveLevels(levelsDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<LevelsDTO.LevelsResponseDTO> updateLevels(@Valid @RequestBody LevelsDTO.LevelsCreateDTO levelsDTO,
                                                                    @PathVariable Long id) {
        return new ResponseEntity<>(levelsService.updateLevels(levelsDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteLevels(@PathVariable Long id) {
        return new ResponseEntity<>(levelsService.deleteLevels(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<LevelsDTO.LevelsResponseDTO>> findAllLevels() {
        return new ResponseEntity<>(levelsService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<LevelsDTO.LevelsResponseDTO> findLevelsById(@PathVariable Long id) {
        return new ResponseEntity<>(levelsService.findLevelsById(id), HttpStatus.OK);
    }
}