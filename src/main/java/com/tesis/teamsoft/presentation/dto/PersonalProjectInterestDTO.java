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
        @NotNull(message = "ERR_VAL_PERSONAL_PROJECT_ID")
        private Long projectId;

        @NotNull(message = "ERR_VAL_PERSONAL_PROJECT_PREFERENCE")
        private Boolean preference;
    }

    @Data
    public static class PersonalProjectInterestResponseDTO {
        private Long id;
        private ProjectDTO.ProjectResponseDTO project;
        private Boolean preference;
    }
}
