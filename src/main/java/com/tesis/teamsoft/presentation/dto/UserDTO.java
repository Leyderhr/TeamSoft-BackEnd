package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDTO {

    @Data
    public static class UserCreateDTO {
        @NotBlank(message = "ERR_VAL_USER_PERSON_NAME")
        @Size(min = 1, message = "ERR_VAL_USER_PERSON_NAME")
        private String personName;

        @NotBlank(message = "ERR_VAL_USER_SURNAME")
        @Size(min = 1, message = "ERR_VAL_USER_SURNAME")
        private String surname;

        @NotBlank(message = "ERR_VAL_USER_ID_CARD")
        @Pattern(regexp = "^\\d{8,}$", message = "ERR_VAL_USER_ID_CARD")
        private String card;

        @NotBlank(message = "ERR_VAL_USER_EMAIL")
        @Email(message = "ERR_VAL_USER_EMAIL")
        private String mail;

        @NotNull(message = "ERR_VAL_USER_ENABLED_STATUS")
        private boolean enabled;

        @NotNull(message = "ERR_VAL_USER_ROLES")
        @Size(min = 1, message = "ERR_VAL_USER_ROLES")
        private Set<Long> roleIds;
    }

    @Data
    public static class UserResponseDTO {
        private Long id;
        private String personName;
        private String surname;
        private String idCard;
        private String mail;
        private String username;
        private boolean enabled;
        private Set<UserRoleDTO.UserRoleResponseDTO> roles;
    }
}