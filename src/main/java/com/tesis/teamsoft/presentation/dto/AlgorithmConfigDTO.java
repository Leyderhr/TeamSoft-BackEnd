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
        @NotNull(message = "The initial solution must not be null")
        private Integer initialSolutionConf;        // 0-4

        @NotNull(message = "The number of trials for obtaining a person in a team role must not be null")
        @Min(value = 1, message = "The number of trials for obtaining a person in a team role must be at least 1")
        @Max(value = 100, message = "The number of trials for obtaining a person in a team role cannot exceed 100")
        private Integer numberPersonTries;

        // Operadores
        @NotNull(message = "Operator to use must not be null")
        private Integer operatorOpc;              // 0-2

        @NotNull(message = "Type of operator must not be null")
        private Integer operatorTypeOpc;           // 0-7 (dinámico)

        // Estrategia
        @NotNull(message = "The number of executions must not be null")
        @Min(value = 1, message = "The number of executions must be at least 1")
        @Max(value = 100, message = "The number of executions cannot exceed 100")
        private Integer executions;

        @NotNull(message = "The number of iterations must not be null")
        @Min(value = 1, message = "The number of iterations must be at least 1")
        @Max(value = 100000, message = "The number of iterations cannot exceed 100000")
        private Integer iterations;

        @NotNull(message = "Calculate Time must not be null")
        private boolean calculateTime;

        @NotNull(message = "Validate must not be null")
        private boolean validate;

        @NotNull(message = "The number of trials to obtain a valid state must not be null")
        @Min(value = 1, message = "The number of trials to obtain a valid state must be at least 1")
        @Max(value = 100, message = "The number of trials to obtain a valid state cannot exceed 100")
        private Integer possibleValidateNumber;

        // Algoritmos
        @NotNull(message = "The number of iterations to restart Hill Climbing with Restart must not be null")
        @Min(value = 1, message = "The number of iterations to restart Hill Climbing with Restart must be at least 1")
        @Max(value = 100, message = "The number of iterations to restart Hill Climbing with Restart cannot exceed 100")
        private Integer hillClimbingRestartCount;

        @NotNull(message = "The Tabu list size must not be null")
        @Min(value = 1, message = "The Tabu list size must be at least 1")
        @Max(value = 100, message = "The Tabu list size cannot exceed 100")
        private Integer tabuSolutionsMaxelements;

        @NotNull(message = "The neighborhood size of the current state for Multi‑objective Hill Climbing with Restart must not be null")
        @Min(value = 1, message = "The neighborhood size of the current state for Multi‑objective Hill Climbing with Restart must be at least 1")
        @Max(value = 100, message = "The neighborhood size of the current state for Multi‑objective Hill Climbing with Restart cannot exceed 20")
        private Integer multiobjectiveHCRestartSizeNeighbors;

        @NotNull(message = "The neighborhood distance of the current state for Multi‑objective Hill Climbing with Restart must not be null")
        @Min(value = 1, message = "The neighborhood distance of the current state for Multi‑objective Hill Climbing with Restart must be at least 1")
        @Max(value = 100, message = "The neighborhood distance of the current state for Multi‑objective Hill Climbing with Restart cannot exceed 5")
        private Integer multiobjectiveHCDistanceSizeNeighbors;

        @NotNull(message = "The multi‑objective Tabu list size must not be null")
        @Min(value = 1, message = "The multi‑objective Tabu list size must be at least 1")
        @Max(value = 100, message = "The multi‑objective Tabu list size cannot exceed 100")
        private Integer multiobjectiveTabuSolutionsMaxelements;
    }
}
