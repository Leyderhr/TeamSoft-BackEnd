package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectRoleDTO {

    @Data
    public static class ProjectRoleCreateDTO {
        @NotNull(message = "Role ID is required")
        private Long role;

        @NotNull(message = "Role Load ID is required")
        private Long roleLoad;

        @NotNull(message = "Amount of workers is required")
        @Min(value = 1, message = "Amount of workers must be at least 1")
        private Long amountWorkersRole;
    }

    @Data
    public static class ProjectRoleResponseDTO {
        private Long id;
        private Long amountWorkersRole;
        private RoleDTO.RoleMinimalDTO role;
        private RoleLoadDTO.RoleLoadResponseDTO roleLoad;

    }
}
