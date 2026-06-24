package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenceImportanceDTO {

    @Data
    public static class CompetenceImportanceCreateDTO {

        @NotNull(message = "ERR_VAL_COMP_IMPORTANCE_LEVEL")
        @Min(value = 0, message = "ERR_VAL_COMP_IMPORTANCE_LEVEL")
        private Long levels;

        @NotBlank(message = "ERR_VAL_COMP_IMPORTANCE_SIGNIFICANCE")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_COMP_IMPORTANCE_SIGNIFICANCE")
        private String significance;
    }

    @Data
    public static class CompetenceImportanceResponseDTO {
        private Long id;
        private Long levels;
        private String significance;
    }
}