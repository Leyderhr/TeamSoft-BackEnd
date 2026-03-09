package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectStructureDTO {

    @Data
    public static class ProjectStructureCreateDTO {
        @NotBlank(message = "Name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String name;

        @Valid
        @NotNull(message = "Project roles are required")
        @Size(min = 1, message = "At least one project role is required")
        private List<ProjectRoleDTO.ProjectRoleCreateDTO> projectRoles;
    }

    @Data
    public static class ProjectStructureResponseDTO {
        private Long id;
        private String name;
        private List<ProjectRoleDTO.ProjectRoleResponseDTO> projectRoles;
    }

    @Data
    public static class ProjectStructureSimpleDTO {
        private Long id;
        private String name;
    }
}