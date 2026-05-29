package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.BossProposalDTO;
import com.tesis.teamsoft.presentation.dto.BossRequestDTO;
import com.tesis.teamsoft.presentation.dto.MembersProposalDTO;
import com.tesis.teamsoft.presentation.dto.MembersRequestDTO;
import com.tesis.teamsoft.service.implementation.TeamFormationStepTwoServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "TeamFormationStepTwo")
@RequestMapping("/teamFormation")
public class TeamFormationStepTwoController {

    private final TeamFormationStepTwoServiceImpl stepTwoService;

    @PostMapping("/boss-proposals")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<BossProposalDTO.BossProposalResponseDTO> getBossProposals(
            @Valid @RequestBody BossRequestDTO.BossProposalRequestDTO request) throws Exception {
        BossProposalDTO.BossProposalResponseDTO response = stepTwoService.getBossProposals(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/member-proposals")
    @PreAuthorize("hasRole('EXPERIMENTADOR') OR hasRole('DIRECTIVO_TECNICO')")
    public ResponseEntity<MembersProposalDTO.MemberProposalResponseDTO> getMemberProposals(
            @Valid @RequestBody MembersRequestDTO.MemberProposalRequestDTO request) throws Exception {
        MembersProposalDTO.MemberProposalResponseDTO response = stepTwoService.getMemberProposals(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}