package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
public class LoginDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDTO {

        @NotBlank(message = "ERR_VAL_LOGIN_USERNAME")
        @Size(min = 3, max = 50, message = "ERR_VAL_LOGIN_USERNAME")
        private String username;

        @NotBlank(message = "ERR_VAL_LOGIN_PASSWORD")
        @Size(min = 6, message = "ERR_VAL_LOGIN_PASSWORD")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDTO {

        private String token;
        private String refreshToken;
        private String type;
        private String username;
        private Set<String> authorities;
        private Long expiresIn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequestDTO {
        @NotBlank(message = "ERR_VAL_LOGIN_REFRESH_TOKEN_REQUIRED")
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenResponseDTO {
        private String accessToken;
        private String refreshToken;
        private String type;
        private Long expiresIn;
    }
}
