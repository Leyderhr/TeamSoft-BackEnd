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
        @NotBlank(message = "ERR_VAL_ROLE_NAME")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_ROLE_NAME")
        private String roleName;

        @NotBlank(message = "VAL_ROLE_DESCRIPTION")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "VAL_ROLE_DESCRIPTION")
        private String roleDesc;

        @NotNull(message = "ERR_VAL_ROLE_IMPACT")
        @Min(value = 0, message = "ERR_VAL_ROLE_IMPACT")
        private Float impact;

        @NotNull(message = "ERR_VAL_ROLE_IS_BOSS")
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
        @NotNull(message = "ERR_VAL_COMPETENCE_REQUIRED")
        private Long competenceId;

        @NotNull(message = "ERR_VAL_COMPETENCE_IMPORTANCE_REQUIRED")
        private Long competenceImportanceId;

        @NotNull(message = "ERR_VAL_LEVEL_REQUIRED")
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