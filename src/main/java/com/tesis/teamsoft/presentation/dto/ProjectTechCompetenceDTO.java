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
        @NotNull(message = "Competence ID is required")
        private Long competenceId;

        @NotNull(message = "Competence Importance ID is required")
        private Long competenceImportanceId;

        @NotNull(message = "Level ID is required")
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