package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonGroupDTO {

    @Data
    public static class PersonGroupCreateDTO {

        @NotBlank(message = "ERR_VAL_PERSON_GROUP_NAME")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_PERSON_GROUP_NAME")
        private String name;

        private Long parentGroupId; // ID del grupo padre (opcional para grupos raíz)
    }

    @Data
    public static class PersonGroupResponseDTO {
        private Long id;
        private String name;
        private String father;
    }
}