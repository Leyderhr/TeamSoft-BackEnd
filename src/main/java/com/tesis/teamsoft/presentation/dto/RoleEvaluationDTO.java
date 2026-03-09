package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleEvaluationDTO {

    @Data
    public static class RoleEvaluationCreateDTO {
        @NotNull(message = "Levels is required")
        @Min(value = 0, message = "Levels must be at least 0")
        private Float levels;

        @NotBlank(message = "Significance is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String significance;
    }

    @Data
    public static class RoleEvaluationResponseDTO {
        private Long id;
        private Float levels;
        private String significance;
    }
}