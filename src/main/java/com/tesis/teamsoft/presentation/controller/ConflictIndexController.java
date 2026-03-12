package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ConflictIndexDTO;
import com.tesis.teamsoft.service.implementation.ConflictIndexServiceImpl;
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
@Tag(name = "ConflictIndex")
@RequestMapping("/conflictIndex")
public class ConflictIndexController {

    private final ConflictIndexServiceImpl conflictIndexService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ConflictIndexDTO.ConflictIndexResponseDTO> createConflictIndex(@Valid @RequestBody ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO) {
        return new ResponseEntity<>(conflictIndexService.saveConflictIndex(conflictIndexDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ConflictIndexDTO.ConflictIndexResponseDTO> updateConflictIndex(@Valid @RequestBody ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO,
                                                                                         @PathVariable Long id) {
        return new ResponseEntity<>(conflictIndexService.updateConflictIndex(conflictIndexDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteConflictIndex(@PathVariable Long id) {
        return new ResponseEntity<>(conflictIndexService.deleteConflictIndex(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<ConflictIndexDTO.ConflictIndexResponseDTO>> findAllConflictIndex() {
        return new ResponseEntity<>(conflictIndexService.findAllByOrderByIdAsc(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ConflictIndexDTO.ConflictIndexResponseDTO> findConflictIndexById(@PathVariable Long id) {
        return new ResponseEntity<>(conflictIndexService.findConflictIndexById(id), HttpStatus.OK);

    }
}