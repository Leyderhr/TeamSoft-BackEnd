package com.tesis.teamsoft.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonTestDTO {

    @Data
    public static class PersonTestCreateDTO {
        @NotNull(message = "ES is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("e_S")
        private Character eS;

        @NotNull(message = "ID is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("i_D")
        private Character iM;

        @NotNull(message = "CO is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("c_O")
        private Character cO;

        @NotNull(message = "IS is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("i_S")
        private Character iS;

        @NotNull(message = "CE is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("c_E")
        private Character cE;

        @NotNull(message = "IR is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("i_R")
        private Character iR;

        @NotNull(message = "ME is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("m_E")
        private Character mE;

        @NotNull(message = "CH is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("c_H")
        private Character cH;

        @NotNull(message = "IF is required")
        @Pattern(regexp = "^[PEI]$", message = "Only P, E or I are allowed")
        @JsonProperty("i_F")
        private Character iF;

        @NotBlank(message = "MBTI test result is required")
        @Pattern(regexp = "^(E|I)(N|S)(F|T)(J|P)$",
                message = "Must be a valid MBTI type like 'ENFJ'")
        private String mbtiType;
    }

    @Data
    public static class PersonTestResponseDTO {
        private Long id;
        private Character eS;
        private Character iM;
        private Character cO;
        private Character iS;
        private Character cE;
        private Character iR;
        private Character mE;
        private Character cH;
        private Character iF;
        private String mbtiType;
    }
}
