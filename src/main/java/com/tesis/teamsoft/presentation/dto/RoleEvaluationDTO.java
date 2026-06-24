package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleEvaluationDTO {

    @Data
    public static class RoleEvaluationCreateDTO {
        @NotNull(message = "ERR_VAL_ROLE_EVAL_LEVEL")
        @Min(value = 0, message = "ERR_VAL_ROLE_EVAL_LEVEL")
        private Float levels;

        @NotBlank(message = "ERR_VAL_ROLE_EVAL_SIGNIFICANCE")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_ROLE_EVAL_SIGNIFICANCE")
        private String significance;
    }

    @Data
    public static class RoleEvaluationResponseDTO {
        private Long id;
        private Float levels;
        private String significance;
    }
}