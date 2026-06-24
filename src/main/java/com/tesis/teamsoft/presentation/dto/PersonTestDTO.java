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
        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_ES")
        @JsonProperty("e_S")
        private Character eS;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_ID")
        @JsonProperty("i_M")
        private Character iM;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_CO")
        @JsonProperty("c_O")
        private Character cO;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_IS")
        @JsonProperty("i_S")
        private Character iS;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_CE")
        @JsonProperty("c_E")
        private Character cE;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_IR")
        @JsonProperty("i_R")
        private Character iR;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_ME")
        @JsonProperty("m_E")
        private Character mE;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_CH")
        @JsonProperty("c_H")
        private Character cH;

        @NotNull(message = "ERR_VAL_PERSON_TEST_BELBIN_IF")
        @JsonProperty("i_F")
        private Character iF;

        @NotBlank(message = "ERR_VAL_PERSON_TEST_MBTI_RESULT")
        @Pattern(regexp = "^(E|I)(N|S)(F|T)(J|P)$",
                message = "ERR_VAL_PERSON_TEST_MBTI_RESULT")
        private String mbtiType;

        @AssertTrue(message = "ERR_VAL_PERSON_TEST_BELBIN_ROLE_VALUE")
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
