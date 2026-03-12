package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.PersonDTO;
import com.tesis.teamsoft.service.implementation.PersonServiceImpl;
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
@Tag(name = "Person")
@RequestMapping("/person")
public class PersonController {

    private final PersonServiceImpl personService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonDTO.PersonResponseDTO> createPerson(@Valid @RequestBody PersonDTO.PersonCreateDTO personDTO) {
            return new ResponseEntity<>(personService.savePerson(personDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonDTO.PersonResponseDTO> updatePerson(@Valid @RequestBody PersonDTO.PersonCreateDTO personDTO,
                                          @PathVariable Long id) {
            return new ResponseEntity<>(personService.updatePerson(personDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deletePerson(@PathVariable Long id) {
            return new ResponseEntity<>(personService.deletePerson(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<PersonDTO.PersonResponseDTO>> findAllPerson() {
            return new ResponseEntity<>(personService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonDTO.PersonResponseDTO> findPersonById(@PathVariable Long id) {
            return new ResponseEntity<>(personService.findPersonById(id), HttpStatus.OK);
    }
}