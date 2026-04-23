package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ClientDTO;
import com.tesis.teamsoft.service.implementation.ClientServiceImpl;
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
@Tag(name = "Client")
@RequestMapping("/clients")
public class ClientController {

    private final ClientServiceImpl clientService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ClientDTO.ClientResponseDTO> createClient(@Valid @RequestBody ClientDTO.ClientCreateDTO clientDTO) {
        return new ResponseEntity<>(clientService.saveClient(clientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ClientDTO.ClientResponseDTO> updateClient(@Valid @RequestBody ClientDTO.ClientCreateDTO clientDTO,
                                                                    @PathVariable Long id) {
        return new ResponseEntity<>(clientService.updateClient(clientDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.deleteClient(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<List<ClientDTO.ClientResponseDTO>> findAllClient() {
        return new ResponseEntity<>(clientService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_RRHH')")
    public ResponseEntity<ClientDTO.ClientResponseDTO> findClientById(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.findClientById(id), HttpStatus.OK);
    }
}