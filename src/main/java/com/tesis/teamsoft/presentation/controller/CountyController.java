package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.CountyDTO;
import com.tesis.teamsoft.service.implementation.CountyServiceImpl;
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
@Tag(name = "County")
@RequestMapping("/county")
public class CountyController {

    private final CountyServiceImpl countyService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CountyDTO.CountyResponseDTO> createCounty(@Valid @RequestBody CountyDTO.CountyCreateDTO countyDTO) {
        return new ResponseEntity<>(countyService.saveCounty(countyDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CountyDTO.CountyResponseDTO> updateCounty(@Valid @RequestBody CountyDTO.CountyCreateDTO countyDTO,
                                                                    @PathVariable Long id) {
        return new ResponseEntity<>(countyService.updateCounty(countyDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteCounty(@PathVariable Long id) {
        return new ResponseEntity<>(countyService.deleteCounty(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<CountyDTO.CountyResponseDTO>> findAllCounty() {
        return new ResponseEntity<>(countyService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CountyDTO.CountyResponseDTO> findCountyById(@PathVariable Long id) {
        return new ResponseEntity<>(countyService.findCountyById(id), HttpStatus.OK);
    }
}