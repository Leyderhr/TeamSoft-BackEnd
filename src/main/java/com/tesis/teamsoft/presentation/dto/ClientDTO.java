package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientDTO {

    @Data
    public static class ClientCreateDTO {
        @NotBlank(message = "Entity name is required")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "Only letters, numbers and spaces are allowed")
        private String entityName;

        @NotBlank(message = "Address is required")
        private String address;

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^(?=(?:\\D*\\d){8})[\\d+\\-\\s]+$",
                message = "Phone can only contain digits, spaces, plus and hyphen and must have at least 8 digits")
        private String phone;
    }

    @Data
    public static class ClientResponseDTO {
        private Long id;
        private String entityName;
        private String address;
        private String phone;
    }
}