package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CostDistanceDTO {

    @Data
    public static class CostDistanceCreateDTO {
        @NotNull(message = "ERR_VAL_COST_DISTANCE_")
        @Min(value = 0, message = "ERR_VAL_COST_DISTANCE_")
        private Float costDistance;

        @NotNull(message = "ERR_VAL_COST_DISTANCE_COUNTY_A_REQUIRED")
        private Long countyAId;

        @NotNull(message = "ERR_VAL_COST_DISTANCE_COUNTY_B_REQUIRED")
        private Long countyBId;

        @AssertTrue(message = "ERR_VAL_COST_DISTANCE_COUNTIES_SAME")
        public boolean isCountiesDifferent() {
            return countyAId != null && countyBId != null && !countyAId.equals(countyBId);
        }
    }

    @Data
    public static class CostDistanceResponseDTO {
        private Long id;
        private Float costDistance;

        private CountyDTO.CountyResponseDTO countyA;
        private CountyDTO.CountyResponseDTO countyB;
    }
}