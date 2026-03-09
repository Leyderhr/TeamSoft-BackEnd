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
public class ConflictIndexDTO {

    @Data
    public static class ConflictIndexCreateDTO {
        @NotBlank(message = "Description is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String description;

        @NotNull(message = "Weight is required")
        @Min(value = 0, message = "Weight must be at least 0")
        private Long weight;
    }

    @Data
    public static class ConflictIndexResponseDTO {
        private Long id;
        private String description;
        private Long weight;
    }
}