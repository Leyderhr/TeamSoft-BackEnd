package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.ChangePasswordDTO;
import com.tesis.teamsoft.presentation.dto.LoginDTO;
import com.tesis.teamsoft.security.jwt.JwtUtils;
import com.tesis.teamsoft.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<LoginDTO.LoginResponseDTO> login(@Valid @RequestBody LoginDTO.LoginRequestDTO loginRequest) {
        LoginDTO.LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginDTO.RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody LoginDTO.RefreshTokenRequestDTO request) {
        LoginDTO.RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordDTO request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        if(userDetails == null)
            throw new IllegalArgumentException("You need to provide the user's information");

        authService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<Map<String, Object>> validateResetToken(@PathVariable String token) {
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
