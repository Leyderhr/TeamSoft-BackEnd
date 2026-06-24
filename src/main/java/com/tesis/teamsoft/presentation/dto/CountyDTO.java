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
        @NotBlank(message = "ERR_VAL_COUNTY_NAME")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "ERR_VAL_COUNTY_NAME")
        private String countyName;

        @NotBlank(message = "ERR_VAL_COUNTY_CODE")
        @Pattern(regexp = "^\\d{1,}$", message = "ERR_VAL_COUNTY_CODE")
        private String code;
    }

    @Data
    public static class CountyResponseDTO {
        private Long id;
        private String countyName;
        private String code;
    }
}