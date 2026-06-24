package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Datos para cerrar un equipo/proyecto:
 *  - roleEvaluation: evaluación del proyecto.
 *  - bossEvaluation: evaluación del jefe de equipo (persona, rol y evaluación).
 */
@Data
public class CloseProjectDTO {

    @NotNull(message = "ERR_VAL_CLOSE_PROJECT_ROLE_EVAL_ID")
    private Long roleEvaluation;

    @Valid
    @NotNull(message = "ERR_VAL_CLOSE_PROJECT_BOSS_EVAL")
    private RolePersonEvaluationDTO bossEvaluation;
}
