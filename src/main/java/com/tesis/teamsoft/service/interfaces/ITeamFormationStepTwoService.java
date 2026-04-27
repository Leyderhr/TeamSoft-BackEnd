package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.BossProposalDTO;
import com.tesis.teamsoft.presentation.dto.BossRequestDTO;
import com.tesis.teamsoft.presentation.dto.MembersProposalDTO;
import com.tesis.teamsoft.presentation.dto.MembersRequestDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeamFormationStepTwoService {
    BossProposalDTO.BossProposalResponseDTO getBossProposals(BossRequestDTO.BossProposalRequestDTO request) throws Exception;
    MembersProposalDTO.MemberProposalResponseDTO getMemberProposals(MembersRequestDTO.MemberProposalRequestDTO request) throws Exception;
}