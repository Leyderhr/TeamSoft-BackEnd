package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.presentation.dto.PersonDTO;
import com.tesis.teamsoft.presentation.dto.PersonGroupDTO;
import com.tesis.teamsoft.presentation.dto.TeamProposalDTO;
import com.tesis.teamsoft.service.implementation.TeamFormationStepThreeImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tesis.teamsoft.presentation.dto.TeamFormationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "TeamFormation")
@RequestMapping("/teamFormation")
public class TeamFormationController {

    private final TeamFormationStepThreeImpl teamFormationStepThree;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping("teams")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<?> getTeams(@RequestBody TeamFormationDTO dto) throws Exception {
        // Las excepciones se delegan al GlobalExceptionHandler para devolver
        // códigos coherentes: IllegalArgumentException -> 400, ResourceNotFoundException -> 404.
        return new ResponseEntity<>(teamFormationStepThree.getTeam(dto.getTeamFormationParameters()
                , dto.getProjectsIDs(), dto.getGroupIDs()), HttpStatus.OK);
    }

    @PostMapping("save_teams")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<?> saveTeams(@RequestBody TeamProposalDTO dto) {
        // Igual que arriba: dejar que el GlobalExceptionHandler traduzca el error
        // y conserve el mensaje (antes se envolvía en RuntimeException -> 500 genérico).
        return new ResponseEntity<>(teamFormationStepThree.saveTeamProposal(dto), HttpStatus.OK);
    }
}
