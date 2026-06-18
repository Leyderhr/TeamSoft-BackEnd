package com.tesis.teamsoft.presentation.dto;

import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonDTO {

    @Data
    public static class PersonCreateDTO {
        @NotBlank(message = "ERR_VAL_PERSON_NAME")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "ERR_VAL_PERSON_NAME")
        private String personName;

        @NotBlank(message = "ERR_VAL_PERSON_ID_CARD")
        @Pattern(regexp = "^\\d+$", message = "ERR_VAL_PERSON_ID_CARD")
        private String card;

        @NotBlank(message = "ERR_VAL_PERSON_SURNAME")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "ERR_VAL_PERSON_SURNAME")
        private String surName;

        @NotBlank(message = "ERR_VAL_PERSON_ADDRESS")
        private String address;

        @NotBlank(message = "ERR_VAL_PERSON_PHONE")
        @Pattern(regexp = "^(?=(?:\\D*\\d){8})[\\d+\\-\\s]+$",
                message = "ERR_VAL_PERSON_PHONE")
        private String phone;

        private Character sex;

        @NotNull(message = "ERR_VAL_PERSON_STATUS")
        private Status status;

        @NotBlank(message = "ERR_VAL_PERSON_EMAIL")
        @Email(message = "ERR_VAL_PERSON_EMAIL")
        private String email;

        @NotNull(message = "ERR_VAL_PERSON_IN_DATE")
        private Date inDate;

        @NotNull(message = "ERR_VAL_PERSON_EXPERIENCE")
        private Integer experience;

        @NotNull(message = "ERR_VAL_PERSON_BIRTH_DATE")
        @Past(message = "ERR_VAL_PERSON_BIRTH_DATE")
        private Date birthDate;

        @NotNull(message = "ERR_VAL_PERSON_GROUP_ID")
        private Long group;

        private Long county;
        private Long race;
        private Long nacionality;
        private Long religion;

        @Valid
        private List<CompetenceValueDTO.CompetenceValueCreateDTO> competenceValues;

        @Valid
        private List<PersonalInterestDTO.PersonalInterestCreateDTO> personalInterests;

        @Valid
        private List<PersonalProjectInterestDTO.PersonalProjectInterestCreateDTO> personalProjectInterests;

        @Valid
        @NotNull(message = "ERR_VAL_PERSON_TEST")
        private PersonTestDTO.PersonTestCreateDTO personTest;

        @Valid
        private List<PersonConflictDTO.PersonConflictCreateDTO> personConflicts;

        @AssertTrue(message = "ERR_VAL_PERSON_SEX")
        public boolean isSexValid() {
            if(sex == null)
                return true;
            return (sex == 'M' || sex == 'F');
        }
    }

    @Data
    public static class PersonResponseDTO {
        private Long id;
        private String personName;
        private String card;
        private String surName;
        private String address;
        private String phone;
        private Character sex;
        private String email;
        private Date inDate;
        private Float workload;
        private Integer experience;
        private String status;
        private Date birthDate;
        private Integer age;
        private CountyDTO.CountyResponseDTO county;
        private RaceDTO.RaceResponseDTO race;
        private PersonGroupDTO.PersonGroupResponseDTO group;
        private NationalityDTO.NacionalityResponseDTO nacionality;
        private ReligionDTO.ReligionResponseDTO religion;
        private AgeGroupDTO.AgeGroupResponseDTO ageGroup;
        private List<CompetenceValueDTO.CompetenceValueResponseDTO> competenceValues;
        private List<PersonalInterestDTO.PersonalInterestResponseDTO> personalInterests;
        private List<PersonalProjectInterestDTO.PersonalProjectInterestResponseDTO> personalProjectInterests;
        private PersonTestDTO.PersonTestResponseDTO personTest;
        private List<PersonConflictDTO.PersonConflictResponseDTO> personConflicts;
    }

    @Data
    public static class PersonMinimalDTO {
        private Long id;
        private String personName;
        private String surName;
        private String card;
    }

    /**
     * DTO para edición parcial (PATCH): solo competencias e incompatibilidades.
     * Cada lista es opcional; null significa "no modificar".
     */
    @Data
    public static class PersonCompetenceConflictPatchDTO {
        @Valid
        private List<CompetenceValueDTO.CompetenceValueCreateDTO> competenceValues;

        @Valid
        private List<PersonConflictDTO.PersonConflictCreateDTO> personConflicts;
    }
}