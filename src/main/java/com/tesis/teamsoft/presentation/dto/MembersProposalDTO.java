package com.tesis.teamsoft.presentation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MembersProposalDTO {
    @Data
    public static class MemberProposalResponseDTO {
        private ProjectDTO.ProjectSimpleDTO project;
        private RoleDTO.RoleMinimalDTO role;
        private List<MemberCandidateDTO> candidates;
    }

    @Data
    public static class MemberCandidateDTO {
        private PersonDTO.PersonMinimalDTO person;
        private float evaluation;
    }
}
