package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ImportDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import com.tesis.teamsoft.service.interfaces.IImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Asistente de Importación de Personas desde CSV (flujo stateless en 2 pasos).
 * Paso 1: subir y analizar el archivo. Paso 2: ejecutar la importación con el mapeo.
 */
@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final IImportService importService;

    /** Paso 1: sube el CSV, valida homogeneidad y devuelve fileId + encabezados + preview. */
    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ImportDTO.ParseResponseDTO> parse(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importService.parse(file));
    }

    /** Paso 2: ejecuta la importación con la configuración de mapeo (transaccional). */
    @PostMapping(value = "/execute", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ImportResultDTO> execute(@Valid @RequestBody ImportDTO.ExecuteRequestDTO request) {
        return ResponseEntity.ok(importService.execute(request));
    }
}
