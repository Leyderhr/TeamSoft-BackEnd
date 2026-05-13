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
        @NotBlank(message = "Project name is required")
        @Size(max = 1024, message = "Project name max length is 1024")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String projectName;

        @NotNull(message = "Initial date is required")
        private Date initialDate;

        @NotNull(message = "Client ID is required")
        private Long client;

        @NotNull(message = "Province ID is required")
        private Long province;

        @NotNull(message = "Project structure ID is required (for cycle creation)")
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
