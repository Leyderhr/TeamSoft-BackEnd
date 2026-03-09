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

        @Min(value = 0, message = "Level must be at least 0")
        @NotNull(message = "Level is required")
        private Long levels;

        @NotBlank(message = "Significance is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String significance;
    }

    @Data
    public static class LevelsResponseDTO {
        private Long id;
        private Long levels;
        private String significance;
    }
}