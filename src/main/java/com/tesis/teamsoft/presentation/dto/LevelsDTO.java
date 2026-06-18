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
public class LevelsDTO {

    @Data
    public static class LevelsCreateDTO {

        @Min(value = 0, message = "ERR_VAL_LEVELS_VALUE")
        @NotNull(message = "ERR_VAL_LEVELS_VALUE")
        private Long levels;

        @NotBlank(message = "ERR_VAL_LEVELS_SIGNIFICANCE")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_LEVELS_SIGNIFICANCE")
        private String significance;
    }

    @Data
    public static class LevelsResponseDTO {
        private Long id;
        private Long levels;
        private String significance;
    }
}