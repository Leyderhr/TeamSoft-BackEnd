package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class PersonalInterestDTO {

    @Data
    public static class PersonalInterestCreateDTO {
        @NotNull(message = "Role ID is required")
        private Long roleId;

        @NotNull(message = "Preference is required")
        private Boolean preference;
    }

    @Data
    public static class PersonalInterestResponseDTO {
        private Long id;
        private RoleDTO.RoleMinimalDTO role;
        private Boolean preference;
    }
}
