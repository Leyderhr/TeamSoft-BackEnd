package com.tesis.teamsoft.presentation.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleDTO {

    @Data
    public static class RoleCreateDTO {
        @NotBlank(message = "Role name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String roleName;

        @NotBlank(message = "Role description is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String roleDesc;

        @NotNull(message = "Impact is required")
        @Min(value = 0, message = "Impact must be at least 0")
        private Float impact;

        @NotNull(message = "Is boss is required")
        private Boolean isBoss;

        @Valid
        private List<RoleCompetitionCreateDTO> roleCompetitions;
        private List<Long> incompatibleRoleIds;
    }

    @Data
    public static class RoleResponseDTO {
        private Long id;
        private String roleName;
        private String roleDesc;
        private Float impact;
        private Boolean isBoss;
        private List<RoleCompetitionResponseDTO> roleCompetitions;
        private List<RoleMinimalDTO> incompatibleRoles;
    }

    @Data
    public static class RoleMinimalDTO {
        private Long id;
        private String roleName;
    }

    @Data
    public static class RoleImportExportDTO{
        @CsvBindByName(column = "name")
        private String roleName;

        @CsvBindByName(column = "description")
        private String roleDesc;

        @CsvBindByName(column = "impact")
        private float impact;

        @CsvBindByName(column = "isBoss")
        private boolean isBoss;
    }

    @Data
    public static class RoleCompetitionCreateDTO {
        @NotNull(message = "Competence ID is required")
        private Long competenceId;

        @NotNull(message = "Competence Importance ID is required")
        private Long competenceImportanceId;

        @NotNull(message = "Levels ID is required")
        private Long levelsId;
    }

    @Data
    public static class RoleCompetitionResponseDTO {
        private Long id;
        private CompetenceDTO.CompetenceMinimalDTO competence;
        private CompetenceImportanceDTO.CompetenceImportanceResponseDTO competenceImportance;
        private LevelsDTO.LevelsResponseDTO level;
    }
}