package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePersonEvaluationDTO {

    @NotNull(message = "Person id is required")
    private Long person;

    @NotNull(message = "Role id is required")
    private Long role;

    @NotNull(message = "Role evaluation id is required")
    private Long roleEvaluation;
}
