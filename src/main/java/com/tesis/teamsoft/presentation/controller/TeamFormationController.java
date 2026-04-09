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
    public ResponseEntity<?> getTeams(@RequestBody TeamFormationDTO dto) {

        try {
            return new ResponseEntity<>(teamFormationStepThree.getTeam(dto.getTeamFormationParameters()
                    , dto.getProjectsIDs(), dto.getGroupIDs()), HttpStatus.OK);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("save_teams")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<?> saveTeams(@RequestBody TeamProposalDTO dto) {
        try{
            return new ResponseEntity<>(teamFormationStepThree.saveTeamProposal(dto), HttpStatus.OK);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
