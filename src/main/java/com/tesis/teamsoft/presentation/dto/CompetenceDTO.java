package com.tesis.teamsoft.presentation.dto;

import com.opencsv.bean.CsvBindByName;
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
        @NotBlank(message = "ERR_VAL_COMPETENCE_NAME")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_COMPETENCE_NAME")
        private String competitionName;

        @NotBlank(message = "ERR_VAL_COMPETENCE_DESCRIPTION")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_COMPETENCE_DESCRIPTION")
        private String description;

        @NotNull(message = "ERR_VAL_COMPETENCE_IS_TECHNICAL")
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

    @Data
    public static class CompetenceImportExportDTO {
        @CsvBindByName(column = "name")
        private String competitionName;

        @CsvBindByName(column = "description")
        private String description;

        @CsvBindByName(column = "technical")
        private boolean technical;
    }
}
