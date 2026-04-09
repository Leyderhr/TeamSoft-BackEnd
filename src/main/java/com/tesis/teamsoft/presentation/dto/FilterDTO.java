package com.tesis.teamsoft.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterDTO {

    @Data
    public static class FilterRequestDTO {
        @Valid private List<PersonalInterestFilterDTO> roleInterests;
        @Valid private List<PersonalProjectInterestFilterDTO> projectInterests;
        @Valid private List<CompetenceValueFilterDTO> competenceLevels;
        @Valid private BelbinFilterDTO belbin;
        @Valid private AgeFilterDTO age;
        private List<String> mbtiTypes;
    }

    @Data
    public static class PersonalInterestFilterDTO {
        @NotNull private Long roleId;
        @NotNull private Boolean preference;
    }

    @Data
    public static class PersonalProjectInterestFilterDTO {
        @NotNull private Long projectId;
        @NotNull private Boolean preference;
    }

    @Data
    public static class CompetenceValueFilterDTO {
        @NotNull private Long competenceId;
        @NotNull private Long levelId;
    }

    @Data
    public static class BelbinFilterDTO {
        @JsonProperty("e_S")
        private Character eS;
        @JsonProperty("i_M")
        private Character iM;
        @JsonProperty("c_O")
        private Character cO;
        @JsonProperty("i_S")
        private Character iS;
        @JsonProperty("c_E")
        private Character cE;
        @JsonProperty("i_R")
        private Character iR;
        @JsonProperty("m_E")
        private Character mE;
        @JsonProperty("c_H")
        private Character cH;
        @JsonProperty("i_F")
        private Character iF;
    }

    @Data
    public static class AgeFilterDTO {
        @NotNull private Integer minAge;
        @NotNull private Integer maxAge;
    }
}
