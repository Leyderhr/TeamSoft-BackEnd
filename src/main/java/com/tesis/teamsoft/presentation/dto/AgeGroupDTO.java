package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgeGroupDTO {

    @Data
    public static class AgeGroupCreateDTO {
        @NotBlank(message = "Age group name is required")
        private String ageGroupName;

        @NotNull(message = "Maximum age is required")
        @Min(value = 0, message = "Maximum age must be at least 0")
        @Max(value = 150, message = "Maximum age cannot exceed 150")
        private int maxAge;

        @NotNull(message = "Minimum age is required")
        @Min(value = 0, message = "Minimum age must be at least 0")
        @Max(value = 150, message = "Minimum age cannot exceed 150")
        private int minAge;


        // Validación personalizada para asegurar que minAge <= maxAge
        @AssertTrue(message = "Minimum age must be less than or equal to maximum age")
        public boolean isAgeRangeValid() {
            return minAge <= maxAge;
        }
    }

    @Data
    public static class AgeGroupResponseDTO {
        private Long id;
        private String ageGroupName;
        private int maxAge;
        private int minAge;
    }
}
