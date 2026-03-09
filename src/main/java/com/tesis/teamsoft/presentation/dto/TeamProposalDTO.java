package com.tesis.teamsoft.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TeamProposalDTO {

    private String formattedEval;
    private List<ProjectDTO.ProjectTeamProposalDTO> projectsProposal;

    public TeamProposalDTO(String formattedEval) {
        this.formattedEval = formattedEval;
        projectsProposal = new ArrayList<>();
    }
}
