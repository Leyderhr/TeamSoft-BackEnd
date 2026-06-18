package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectRoleDTO {

    @Data
    public static class ProjectRoleCreateDTO {
        @NotNull(message = "ERR_VAL_PROJECT_ROLE_ID")
        private Long role;

        @NotNull(message = "ERR_VAL_PROJECT_ROLE_LOAD_ID")
        private Long roleLoad;

        @NotNull(message = "ERR_VAL_PROJECT_ROLE_WORKERS")
        @Min(value = 1, message = "ERR_VAL_PROJECT_ROLE_WORKERS")
        private Long amountWorkersRole;

        @Valid
        private List<ProjectTechCompetenceDTO.ProjectTechCompetenceCreateDTO> techCompetences;
    }

    @Data
    public static class ProjectRoleResponseDTO {
        private Long id;
        private Long amountWorkersRole;
        private RoleDTO.RoleMinimalDTO role;
        private RoleLoadDTO.RoleLoadResponseDTO roleLoad;
        private List<ProjectTechCompetenceDTO.ProjectTechCompetenceResponseDTO> techCompetences;
    }
}
