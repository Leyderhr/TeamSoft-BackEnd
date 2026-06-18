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
        @NotBlank(message = "ERR_VAL_AGE_GROUP_NAME")
        private String ageGroupName;

        @NotNull(message = "ERR_VAL_AGE_GROUP_MAX_AGE")
        @Min(value = 0, message = "ERR_VAL_AGE_GROUP_MAX_AGE")
        @Max(value = 150, message = "ERR_VAL_AGE_GROUP_MAX_AGE")
        private int maxAge;

        @NotNull(message = "ERR_VAL_AGE_GROUP_MIN_AGE")
        @Min(value = 0, message = "ERR_VAL_AGE_GROUP_MIN_AGE")
        @Max(value = 150, message = "ERR_VAL_AGE_GROUP_MIN_AGE")
        private int minAge;


        // Validación personalizada para asegurar que minAge <= maxAge
        @AssertTrue(message = "ERR_VAL_AGE_GROUP_RANGE_INVALID")
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
