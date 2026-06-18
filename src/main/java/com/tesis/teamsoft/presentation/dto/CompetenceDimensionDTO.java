package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenceDimensionDTO {

    @Data
    public static class CompetenceDimensionCreateDTO{
        @NotBlank(message = "ERR_VAL_COMP_DIMENSION_NAME")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "ERR_VAL_COMP_DIMENSION_NAME")
        private String name;

        @NotNull(message = "ERR_VAL_LEVEL_REQUIRED")
        private Long levelsID;
    }

    @Data
    public static class CompetenceDimensionResponseDTO{
        private String name;
        private Long competenceID;
        private LevelsDTO.LevelsResponseDTO levelsFk;
    }
}
