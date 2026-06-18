package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgorithmConfigDTO {

    @Data
    public static class AlgorithmConfigUpdateDTO {
        // Configuración inicial
        @NotNull(message = "ERR_VAL_ALGO_INITIAL_SOLUTION_REQUIRED")
        private Integer initialSolutionConf;        // 0-4

        @NotNull(message = "ERR_VAL_ALGO_TRIALS_ROLE")
        @Min(value = 1, message = "ERR_VAL_ALGO_TRIALS_ROLE")
        @Max(value = 100, message = "ERR_VAL_ALGO_TRIALS_ROLE")
        private Integer numberPersonTries;

        // Operadores
        @NotNull(message = "ERR_VAL_ALGO_OPERATOR_REQUIRED")
        private Integer operatorOpc;              // 0-2

        @NotNull(message = "ERR_VAL_ALGO_OPERATOR_TYPE_REQUIRED")
        private Integer operatorTypeOpc;           // 0-7 (dinámico)

        // Estrategia
        @NotNull(message = "ERR_VAL_ALGO_EXECUTIONS")
        @Min(value = 1, message = "ERR_VAL_ALGO_EXECUTIONS")
        @Max(value = 100, message = "ERR_VAL_ALGO_EXECUTIONS")
        private Integer executions;

        @NotNull(message = "ERR_VAL_ALGO_ITERATIONS")
        @Min(value = 1, message = "ERR_VAL_ALGO_ITERATIONS")
        @Max(value = 100000, message = "ERR_VAL_ALGO_ITERATIONS")
        private Integer iterations;

        @NotNull(message = "ERR_VAL_ALGO_CALCULATE_TIME_REQUIRED")
        private boolean calculateTime;

        @NotNull(message = "ERR_VAL_ALGO_VALIDATE_REQUIRED")
        private boolean validate;

        @NotNull(message = "ERR_VAL_ALGO_TRIALS_STATE")
        @Min(value = 1, message = "ERR_VAL_ALGO_TRIALS_STATE")
        @Max(value = 100, message = "ERR_VAL_ALGO_TRIALS_STATE")
        private Integer possibleValidateNumber;

        // Algoritmos
        @NotNull(message = "ERR_VAL_ALGO_HC_RESTART_ITER")
        @Min(value = 1, message = "ERR_VAL_ALGO_HC_RESTART_ITER")
        @Max(value = 100, message = "ERR_VAL_ALGO_HC_RESTART_ITER")
        private Integer hillClimbingRestartCount;

        @NotNull(message = "ERR_VAL_ALGO_TABU_SIZE")
        @Min(value = 1, message = "ERR_VAL_ALGO_TABU_SIZE")
        @Max(value = 100, message = "ERR_VAL_ALGO_TABU_SIZE")
        private Integer tabuSolutionsMaxelements;

        @NotNull(message = "ERR_VAL_ALGO_MOHC_NB_SIZE")
        @Min(value = 1, message = "ERR_VAL_ALGO_MOHC_NB_SIZE")
        @Max(value = 100, message = "ERR_VAL_ALGO_MOHC_NB_SIZE")
        private Integer multiobjectiveHCRestartSizeNeighbors;

        @NotNull(message = "ERR_VAL_ALGO_MOHC_NB_DIST")
        @Min(value = 1, message = "ERR_VAL_ALGO_MOHC_NB_DIST")
        @Max(value = 100, message = "ERR_VAL_ALGO_MOHC_NB_DIST")
        private Integer multiobjectiveHCDistanceSizeNeighbors;

        @NotNull(message = "ERR_VAL_ALGO_MO_TABU_SIZE")
        @Min(value = 1, message = "ERR_VAL_ALGO_MO_TABU_SIZE")
        @Max(value = 100, message = "ERR_VAL_ALGO_MO_TABU_SIZE")
        private Integer multiobjectiveTabuSolutionsMaxelements;
    }
}
