package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.CompetenceDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import com.tesis.teamsoft.service.implementation.CompetenceServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Competence")
@RequestMapping("/competence")
public class CompetenceController {


    private final CompetenceServiceImpl competenceService;


    @PostMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceDTO.CompetenceResponseDTO> createCompetence(@Valid @RequestBody CompetenceDTO.CompetenceCreateDTO competenceDTO) {
        return new ResponseEntity<>(competenceService.saveCompetence(competenceDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceDTO.CompetenceResponseDTO> updateCompetence(@Valid @RequestBody CompetenceDTO.CompetenceCreateDTO competenceDTO,
                                                                                @PathVariable Long id) {
        return new ResponseEntity<>(competenceService.updateCompetence(competenceDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteCompetence(@PathVariable Long id) {
        return new ResponseEntity<>(competenceService.deleteCompetence(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<CompetenceDTO.CompetenceResponseDTO>> findAllCompetence() {
        return new ResponseEntity<>(competenceService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<CompetenceDTO.CompetenceResponseDTO> findCompetenceById(@PathVariable Long id) {
        return new ResponseEntity<>(competenceService.findCompetenceById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ImportResultDTO> importCompetences(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean updateIfExist) {
        try {
            ImportResultDTO result = competenceService.importCompetences(file.getInputStream(), updateIfExist);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }

    @GetMapping(value = "/export", produces = "text/csv")
    //@PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<byte[]> exportCompetences() {
        InputStream is = competenceService.exportCompetences();
        byte[] bytes;
        try {
            bytes = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=competencias.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
