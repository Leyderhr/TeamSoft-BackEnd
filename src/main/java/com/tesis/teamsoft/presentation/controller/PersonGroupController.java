package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.PersonGroupDTO;
import com.tesis.teamsoft.service.implementation.PersonGroupServiceImpl;
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
@Tag(name = "PersonGroup")
@RequestMapping("/personGroups")
public class PersonGroupController {

    private final PersonGroupServiceImpl personGroupService;

    @PostMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonGroupDTO.PersonGroupResponseDTO> createPersonGroup(@Valid @RequestBody PersonGroupDTO.PersonGroupCreateDTO personGroupDTO) {
        return new ResponseEntity<>(personGroupService.savePersonGroup(personGroupDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonGroupDTO.PersonGroupResponseDTO> updatePersonGroup(@Valid @RequestBody PersonGroupDTO.PersonGroupCreateDTO personGroupDTO,
                                                                                   @PathVariable Long id) {
        return new ResponseEntity<>(personGroupService.updatePersonGroup(personGroupDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deletePersonGroup(@PathVariable Long id) {
        return new ResponseEntity<>(personGroupService.deletePersonGroup(id), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<PersonGroupDTO.PersonGroupResponseDTO>> findAllPersonGroup() {
        return new ResponseEntity<>(personGroupService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<PersonGroupDTO.PersonGroupResponseDTO> findPersonGroupById(@PathVariable Long id) {
        return new ResponseEntity<>(personGroupService.findPersonGroupById(id), HttpStatus.OK);
    }
}