package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.RaceDTO;
import com.tesis.teamsoft.service.implementation.RaceServiceImpl;
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
@Tag(name = "Race")
@RequestMapping("/race")
public class RaceController {

    private final RaceServiceImpl raceService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RaceDTO.RaceResponseDTO> createRace(@Valid @RequestBody RaceDTO.RaceCreateDTO raceDTO) {
            return new ResponseEntity<>(raceService.saveRace(raceDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RaceDTO.RaceResponseDTO> updateRace(@Valid @RequestBody RaceDTO.RaceCreateDTO raceDTO,
                                        @PathVariable Long id) {
            return new ResponseEntity<>(raceService.updateRace(raceDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteRace(@PathVariable Long id) {
            return new ResponseEntity<>(raceService.deleteRace(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<RaceDTO.RaceResponseDTO>> findAllRace() {
            return new ResponseEntity<>(raceService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<RaceDTO.RaceResponseDTO> findRaceById(@PathVariable Long id) {
            return new ResponseEntity<>(raceService.findRaceById(id), HttpStatus.OK);
    }
}