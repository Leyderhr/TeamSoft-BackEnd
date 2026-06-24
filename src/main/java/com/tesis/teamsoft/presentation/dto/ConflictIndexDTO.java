package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConflictIndexDTO {

    @Data
    public static class ConflictIndexCreateDTO {
        @NotBlank(message = "ERR_VAL_CONFLICT_INDEX_DESCRIPTION")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_CONFLICT_INDEX_DESCRIPTION")
        private String description;

        @NotNull(message = "ERR_VAL_CONFLICT_INDEX_WEIGHT")
        @Min(value = 0, message = "ERR_VAL_CONFLICT_INDEX_WEIGHT")
        private Long weight;
    }

    @Data
    public static class ConflictIndexResponseDTO {
        private Long id;
        private String description;
        private Long weight;
    }
}