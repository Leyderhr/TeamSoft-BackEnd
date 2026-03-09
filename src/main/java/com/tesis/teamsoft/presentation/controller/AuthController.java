package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.LoginDTO;
import com.tesis.teamsoft.security.jwt.JwtUtils;
import com.tesis.teamsoft.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO.LoginRequestDTO loginRequest) {
        try {
            LoginDTO.LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody LoginDTO.RefreshTokenRequestDTO request) {
        try {
            LoginDTO.RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }
    }

//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO request) {
//        try {
//            authService.processForgotPassword(request.getEmail());
//
//            // Siempre devolver el mismo mensaje por seguridad
//            Map<String, String> response = new HashMap<>();
//            response.put("Message", "If an account exists with this email, you will receive reset instructions within a few minutes.");
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error processing forgot password for email: {}", request.getEmail(), e);
//            // Aún así devolver el mismo mensaje para no revelar información
//            return ResponseEntity.ok(Map.of(
//                    "Message", "If an account exists with this email, you will receive reset instructions within a few minutes."
//            ));
//        }
//    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
//        try {
//            // Validar que las contraseñas coincidan
//            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("Error", "Passwords do not match"));
//            }
//
//            authService.resetPassword(request.getToken(), request.getNewPassword());
//
//            return ResponseEntity.ok(Map.of(
//                    "Message", "Password has been reset successfully. You can now login with your new password."
//            ));
//
//        } catch (IllegalArgumentException e) {
//            log.warn("Invalid password reset attempt: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Invalid or expired token"));
//        } catch (Exception e) {
//            log.error("Error resetting password", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("Error", "An error occurred while resetting password"));
//        }
//    }

//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(
//            @RequestHeader("Authorization") String authHeader,
//            @Valid @RequestBody ResetPasswordDTO request) {
//
//        try {
//            // Extraer username del token JWT (asumiendo que tienes un metodo para esto)
//            String token = authHeader.substring(7); // Remover "Bearer "
//            // Necesitarías inyectar JwtUtils para extraer el username
//            //String username = jwtUtils.extractUsername(Base64.de);
//
//            // Por ahora, usaremos un endpoint protegido que obtiene el usuario del contexto de seguridad
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
//                    .body(Map.of("message", "Change password endpoint to be implemented"));
//
//        } catch (Exception e) {
//            log.error("Error changing password", e);
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Failed to change password"));
//        }
//    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<?> validateResetToken(@PathVariable String token) {
        try {
            // Este endpoint sería usado por el frontend para validar si un token es válido
            // antes de mostrar el formulario de reset de contraseña
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "Token is valid"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Token is invalid or expired"
            ));
        }
    }
}
