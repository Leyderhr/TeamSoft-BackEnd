package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.NationalityDTO;
import com.tesis.teamsoft.service.implementation.NacionalityServiceImpl;
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
@Tag(name = "Nacionality")
@RequestMapping("/nacionality")
public class NacionalityController {

    private final NacionalityServiceImpl nacionalityService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<NationalityDTO.NacionalityResponseDTO> createNacionality(@Valid @RequestBody NationalityDTO.NacionalityCreateDTO nacionalityDTO) {
            return new ResponseEntity<>(nacionalityService.saveNacionality(nacionalityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<NationalityDTO.NacionalityResponseDTO> updateNacionality(@Valid @RequestBody NationalityDTO.NacionalityCreateDTO nacionalityDTO,
                                               @PathVariable Long id) {
            return new ResponseEntity<>(nacionalityService.updateNacionality(nacionalityDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteNacionality(@PathVariable Long id) {
            return new ResponseEntity<>(nacionalityService.deleteNacionality(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<NationalityDTO.NacionalityResponseDTO>> findAllNacionality() {
            return new ResponseEntity<>(nacionalityService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<NationalityDTO.NacionalityResponseDTO> findNacionalityById(@PathVariable Long id) {
            return new ResponseEntity<>(nacionalityService.findNacionalityById(id), HttpStatus.OK);
    }
}