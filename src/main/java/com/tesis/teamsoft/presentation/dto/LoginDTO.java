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

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
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
        @NotBlank(message = "Refresh token is required")
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
