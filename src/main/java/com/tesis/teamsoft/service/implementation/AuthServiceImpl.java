package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.RefreshTokenEntity;
import com.tesis.teamsoft.persistence.entity.UserEntity;
import com.tesis.teamsoft.persistence.repository.IUserRepository;
import com.tesis.teamsoft.presentation.dto.LoginDTO;
import com.tesis.teamsoft.security.jwt.JwtUtils;
import com.tesis.teamsoft.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserRepository userRepository;
    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceImpl refreshTokenService;


    @Override
    public LoginDTO.LoginResponseDTO login(LoginDTO.LoginRequestDTO loginDTO) {

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.createToken(authentication);

            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(
                    authentication.getName()
            );

            log.info("Login successful for user {}", loginDTO.getUsername());

            return LoginDTO.LoginResponseDTO.builder()
                    .token(jwt)
                    .refreshToken(refreshToken.getToken())
                    .type("Bearer")
                    .username(authentication.getName())
                    .authorities(authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()))
                    .expiresIn(1800000L)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {} - Invalid credentials", loginDTO.getUsername());
            throw new BadCredentialsException("Invalid credentials or password");
        }
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            refreshTokenService.deleteByUserId(user.getId());
            log.info("All refresh tokens revoked for user: {}", username);
        }
        
        SecurityContextHolder.clearContext();
        log.info("Logout successful");
    }

    @Override
    @Transactional
    public LoginDTO.RefreshTokenResponseDTO refreshToken(String requestRefreshToken) {
        try {
            RefreshTokenEntity refreshToken = refreshTokenService.findByToken(requestRefreshToken);
            
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            UserEntity user = refreshToken.getUser();
            
            String newAccessToken = jwtUtils.createToken(user.getUsername());
            
            refreshTokenService.revokeToken(requestRefreshToken);
            RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());
            
            log.info("Token refreshed successfully for user: {}", user.getUsername());
            
            return LoginDTO.RefreshTokenResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .type("Bearer")
                    .expiresIn(1800000L)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }

    @Override
    @Transactional
    public void processForgotPassword(String email) {

//        try{
//            UserEntity user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//            String token = passwordResetTokenService.createPasswordResetToken(user);
//            String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
//
//            emailService.sendPasswordResetEmail(email, user.getUsername(), resetLink);
//
//            log.info("Password reset token created for user {}", user.getUsername());
//
//        } catch (UsernameNotFoundException e) {
//            // Por seguridad, no revelamos si el email existe o no
//            log.info("Password reset requested for non-existing email: {}", email);
//        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        passwordResetTokenService.resetPassword(token, newPassword);
    }

    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user {}", user.getUsername());
    }
}
