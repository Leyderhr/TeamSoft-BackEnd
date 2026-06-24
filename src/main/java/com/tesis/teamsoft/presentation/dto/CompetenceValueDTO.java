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
        @NotNull(message = "ERR_VAL_COMPETENCE_REQUIRED")
        private Long competenceId;

        @NotNull(message = "ERR_VAL_LEVEL_REQUIRED")
        private Long levelsId;
    }

    @Data
    public static class CompetenceValueResponseDTO {
        private Long id;
        private CompetenceDTO.CompetenceMinimalDTO competence;
        private LevelsDTO.LevelsResponseDTO level;
    }

}
