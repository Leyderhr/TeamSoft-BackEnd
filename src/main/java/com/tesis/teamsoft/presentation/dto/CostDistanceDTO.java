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
        @NotNull(message = "Cost distance is required")
        @Min(value = 0, message = "Cost distance must be at least 0")
        private Float costDistance;

        @NotNull(message = "County A is required")
        private Long countyAId;

        @NotNull(message = "County B is required")
        private Long countyBId;

        @AssertTrue(message = "County A and County B must be different")
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