package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectTechCompetenceDTO {

    @Data
    public static class ProjectTechCompetenceCreateDTO {
        @NotNull(message = "ERR_VAL_COMPETENCE_REQUIRED")
        private Long competenceId;

        @NotNull(message = "ERR_VAL_COMPETENCE_IMPORTANCE_REQUIRED")
        private Long competenceImportanceId;

        @NotNull(message = "ERR_VAL_LEVEL_REQUIRED")
        private Long levelId;
    }

    @Data
    public static class ProjectTechCompetenceResponseDTO {
        private Long id;
        private CompetenceDTO.CompetenceMinimalDTO competence;
        private CompetenceImportanceDTO.CompetenceImportanceResponseDTO importance;
        private LevelsDTO.LevelsResponseDTO level;
    }
}