package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountyDTO {

    @Data
    public static class CountyCreateDTO {
        @NotBlank(message = "County name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String countyName;

        @NotBlank(message = "Code is required")
        @Pattern(regexp = "^\\d{8,}$", message = "Code must contain at least 8 digits")
        private String code;
    }

    @Data
    public static class CountyResponseDTO {
        private Long id;
        private String countyName;
        private String code;
    }
}