package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.presentation.dto.TeamProposalDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface ITeamFormationStepThreeService {

    List<TeamProposalDTO> getTeam(TeamFormationParameters parameters, List<Long> projectsIDs, @RequestBody List<Long> groupIDs) throws Exception;
}
