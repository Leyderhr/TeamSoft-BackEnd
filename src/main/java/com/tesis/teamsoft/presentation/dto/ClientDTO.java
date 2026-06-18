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
        @NotBlank(message = "ERR_VAL_CLIENT_NAME")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "ERR_VAL_CLIENT_NAME")
        private String entityName;

        @NotBlank(message = "ERR_VAL_CLIENT_ADDRESS")
        private String address;

        @NotBlank(message = "ERR_VAL_CLIENT_PHONE")
        @Pattern(regexp = "^(?=(?:\\D*\\d){8})[\\d+\\-\\s]+$",
                message = "ERR_VAL_CLIENT_PHONE")
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