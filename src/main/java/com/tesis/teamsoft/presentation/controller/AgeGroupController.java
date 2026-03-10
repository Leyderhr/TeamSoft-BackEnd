package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.service.implementation.AgeGroupServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "AgeGroups")
@RequestMapping("/ageGroups")
public class AgeGroupController {


    private final AgeGroupServiceImpl ageGroupService;

    @PostMapping()
    //@PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<?> createAgeGroup(@Valid @RequestBody AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<>(ageGroupService.saveAgeGroup(ageGroupDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<?> updateAgeGroup(@Valid @RequestBody AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO
            , BindingResult bindingResult
            , @PathVariable long id) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<>(ageGroupService.updateAgeGroup(ageGroupDTO, id), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<?> deleteAgeGroup(@PathVariable Long id) {

        try {
            return new ResponseEntity<>(ageGroupService.deleteAgeGroup(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }

    @GetMapping()
    //@PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<?> findAllAgeGroup() {
        try {
            return new ResponseEntity<>(ageGroupService.findAllByOrderByIdAsc(), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error> ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<?> findAgeGroupById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(ageGroupService.findAgeGroupById(id), HttpStatus.OK);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}