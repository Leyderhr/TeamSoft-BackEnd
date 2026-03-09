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
        @NotBlank(message = "Competence Dimension name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String name;

        @NotNull(message = "Levels ID is required")
        private Long levelsID;
    }

    @Data
    public static class CompetenceDimensionResponseDTO{
        private String name;
        private Long competenceID;
        private LevelsDTO.LevelsResponseDTO levelsFk;
    }
}
