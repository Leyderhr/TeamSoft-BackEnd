package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RaceDTO {

    @Data
    public static class RaceCreateDTO {
        @NotBlank(message = "Race name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String raceName;
    }

    @Data
    public static class RaceResponseDTO {
        private Long id;
        private String raceName;
    }
}