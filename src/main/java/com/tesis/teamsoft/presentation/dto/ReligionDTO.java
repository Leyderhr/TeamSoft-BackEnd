package com.tesis.teamsoft.presentation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReligionDTO {


    @Data
    public static class ReligionCreateDTO {
        @NotBlank(message = "Religion name is required")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Only letters and spaces are allowed")
        private String religionName;

    }

    @Data
    public static class ReligionResponseDTO {
        private Long id;
        private String religionName;
    }


}
