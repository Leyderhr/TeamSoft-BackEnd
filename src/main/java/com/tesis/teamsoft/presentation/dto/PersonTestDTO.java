package com.tesis.teamsoft.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
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
        @JsonProperty("e_S")
        private Character eS;

        @NotNull(message = "ID is required")
        @JsonProperty("i_D")
        private Character iM;

        @NotNull(message = "CO is required")
        @JsonProperty("c_O")
        private Character cO;

        @NotNull(message = "IS is required")
        @JsonProperty("i_S")
        private Character iS;

        @NotNull(message = "CE is required")
        @JsonProperty("c_E")
        private Character cE;

        @NotNull(message = "IR is required")
        @JsonProperty("i_R")
        private Character iR;

        @NotNull(message = "ME is required")
        @JsonProperty("m_E")
        private Character mE;

        @NotNull(message = "CH is required")
        @JsonProperty("c_H")
        private Character cH;

        @NotNull(message = "IF is required")
        @JsonProperty("i_F")
        private Character iF;

        @NotBlank(message = "MBTI test result is required")
        @Pattern(regexp = "^(E|I)(N|S)(F|T)(J|P)$",
                message = "Must be a valid MBTI type like 'ENFJ'")
        private String mbtiType;

        @AssertTrue(message = "Belbin roles only admit P, E, or I – nothing else is allowed.")
        public boolean isBelbinRolesValid() {
            return ((eS == 'P' || eS == 'E' || eS == 'I') &&
                    (iM == 'P' || iM == 'E' || iM == 'I') &&
                    (cO == 'P' || cO == 'E' || cO == 'I') &&
                    (iS == 'P' || iS == 'E' || iS == 'I') &&
                    (cE == 'P' || cE == 'E' || cE == 'I') &&
                    (iR == 'P' || iR == 'E' || iR == 'I') &&
                    (mE == 'P' || mE == 'E' || mE == 'I') &&
                    (cH == 'P' || cH == 'E' || cH == 'I') &&
                    (iF == 'P' || iF == 'E' || iF == 'I'));
        }
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
