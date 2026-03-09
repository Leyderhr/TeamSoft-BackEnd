package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NationalityDTO {

    @Data
    public static class NacionalityCreateDTO {
        @NotBlank(message = "Country name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String paisNac;

        @NotBlank(message = "Demonym is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String gentilicioNac;
    }

    @Data
    public static class NacionalityResponseDTO {
        private Long id;
        private String paisNac;
        private String gentilicioNac;
    }
}