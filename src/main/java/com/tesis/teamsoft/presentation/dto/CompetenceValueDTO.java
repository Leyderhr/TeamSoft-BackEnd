package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenceValueDTO {

    @Data
    public static class CompetenceValueCreateDTO {
        @NotNull(message = "Competence ID is required")
        private Long competenceId;

        @NotNull(message = "Levels ID is required")
        private Long levelsId;
    }

    @Data
    public static class CompetenceValueResponseDTO {
        private Long id;
        private CompetenceDTO.CompetenceMinimalDTO competence;
        private LevelsDTO.LevelsResponseDTO level;
    }

}
