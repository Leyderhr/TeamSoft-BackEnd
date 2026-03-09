package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalProjectInterestDTO {

    @Data
    public static class PersonalProjectInterestCreateDTO {
        @NotNull(message = "Project ID is required")
        private Long projectId;

        @NotNull(message = "Preference is required")
        private Boolean preference;
    }

    @Data
    public static class PersonalProjectInterestResponseDTO {
        private Long id;
        private ProjectDTO.ProjectResponseDTO project;
        private Boolean preference;
    }
}
