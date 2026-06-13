package com.tesis.teamsoft.presentation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BossProposalDTO {

    @Data
    public static class BossProposalResponseDTO {
        private List<ProjectBossDTO> proposals;
    }

    @Data
    public static class ProjectBossDTO {
        private ProjectDTO.ProjectSimpleDTO project;
        private RoleDTO.RoleMinimalDTO role;
        private List<BossCandidateDTO> candidates;
    }

    @Data
    public static class BossCandidateDTO {
        private PersonDTO.PersonMinimalDTO person;
        private float evaluation;
    }
}
