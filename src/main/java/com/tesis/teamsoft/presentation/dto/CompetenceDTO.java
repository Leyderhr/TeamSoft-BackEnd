package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenceDTO {

    @Data
    public static class CompetenceCreateDTO{
        @NotBlank(message = "Competence name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String competitionName;

        @NotBlank(message = "Competence description is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String description;

        @NotNull(message = "Define if technical competency")
        private Boolean technical;

        @Valid
        List<CompetenceDimensionDTO.CompetenceDimensionCreateDTO> dimensionList;
    }

    @Data
    public static class CompetenceResponseDTO{
        private Long id;
        private String competitionName;
        private String description;
        private Boolean technical;

        List<CompetenceDimensionDTO.CompetenceDimensionResponseDTO> dimensionList;
    }

    @Data
    public static class CompetenceMinimalDTO {
        private Long id;
        private String competitionName;
        private String description;
        private Boolean technical;
    }
}
