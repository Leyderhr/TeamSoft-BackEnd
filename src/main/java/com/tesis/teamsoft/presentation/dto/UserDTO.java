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
        @NotBlank(message = "Person name is required")
        @Size(min = 1, message = "Person name must be at least 3 characters")
        private String personName;

        @NotBlank(message = "Surname is required")
        @Size(min = 1, message = "Person surname must be at least 3 characters")
        private String surname;

        @NotBlank(message = "ID card is required")
        @Pattern(regexp = "^[0-9+\\-\\s]+$", message = "Phone can only contain digits, spaces, plus and hyphen")
        private String idCard;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String mail;

        @NotNull(message = "Enabled status is required")
        private boolean enabled;

        @NotNull(message = "At least one role is required")
        @Size(min = 1, message = "At least one role must be selected")
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