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
public class RoleLoadDTO {

    @Data
    public static class RoleLoadCreateDTO {
        @NotNull(message = "ERR_VAL_ROLE_LOAD_VALUE")
        @Min(value = 0, message = "ERR_VAL_ROLE_LOAD_VALUE")
        private Float value;

        @NotBlank(message = "ERR_VAL_ROLE_LOAD_SIGNIFICANCE")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_ROLE_LOAD_SIGNIFICANCE")
        private String significance;
    }

    @Data
    public static class RoleLoadResponseDTO {
        private Long id;
        private Float value;
        private String significance;
    }
}