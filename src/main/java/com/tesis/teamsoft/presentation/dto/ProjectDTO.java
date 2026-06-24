package com.tesis.teamsoft.presentation.dto;

import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDTO {

    @Data
    public static class ProjectCreateDTO {
        @NotBlank(message = "ERR_VAL_PROJECT_NAME")
        @Size(max = 1024, message = "ERR_VAL_PROJECT_NAME")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_PROJECT_NAME")
        private String projectName;

        @NotNull(message = "ERR_VAL_PROJECT_INITIAL_DATE")
        private Date initialDate;

        @NotNull(message = "ERR_VAL_PROJECT_CLIENT_ID")
        private Long client;

        @NotNull(message = "ERR_VAL_PROJECT_PROVINCE_ID")
        private Long province;

        @NotNull(message = "ERR_VAL_PROJECT_STRUCTURE_ID")
        private Long projectStructure;
    }

    @Data
    public static class ProjectResponseDTO {
        private Long id;
        private String projectName;
        private Date initialDate;
        private Date endDate;
        private ProjectState state;

        // Relaciones (solo IDs o DTOs mínimos)
        private ClientDTO.ClientResponseDTO client;
        private CountyDTO.CountyResponseDTO county;
        private ProjectStructureDTO.ProjectStructureSimpleDTO projectStructure;
    }

    @Data
    public static class ProjectSimpleDTO {
        private Long id;
        private String projectName;
        private Date initialDate;
        private ProjectState state;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectBossCompetitionsDTO{
        private Long projectId;
        private String projectName;

        private List<RoleDTO.RoleCompetitionResponseDTO> technicalCompetitions;
        private List<RoleDTO.RoleCompetitionResponseDTO> nonTechnicalCompetitions;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectNonBossRolesDTO {
        private Long projectId;
        private String projectName;
        private List<RoleDTO.RoleMinimalDTO> nonBossRoles;
    }

    @Getter
    @Setter
    public static class ProjectTeamProposalDTO {
        private ProjectDTO.ProjectSimpleDTO project;
        List<AssignedRoleDTO> assignedRoles;

        public ProjectTeamProposalDTO(ProjectDTO.ProjectSimpleDTO project) {
            this.project = project;
            assignedRoles = new ArrayList<>();
        }
    }
}
