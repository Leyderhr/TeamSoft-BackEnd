package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.FilterDTO;
import com.tesis.teamsoft.presentation.dto.PersonDTO;
import com.tesis.teamsoft.service.implementation.FilterServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterServiceImpl filterService;

    @PostMapping
    public ResponseEntity<List<PersonDTO.PersonResponseDTO>> filterPersons(@RequestBody FilterDTO.FilterRequestDTO filterRequest) {
        List<PersonDTO.PersonResponseDTO> result = filterService.filterPersons(filterRequest);
        return ResponseEntity.ok(result);
    }
}