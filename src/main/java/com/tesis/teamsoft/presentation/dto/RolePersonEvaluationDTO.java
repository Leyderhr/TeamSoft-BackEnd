package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePersonEvaluationDTO {

    @NotNull(message = "ERR_VAL_PERSON_REQUIRED")
    private Long person;

    @NotNull(message = "ERR_VAL_ROLE_EVAL_REQUIRED")
    private Long role;

    @NotNull(message = "ERR_VAL_ROLE_EVAL_REQUIRED")
    private Long roleEvaluation;
}
