package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.PasswordResetTokenEntity;
import com.tesis.teamsoft.persistence.entity.UserEntity;
import com.tesis.teamsoft.persistence.repository.IPasswordResetTokenRepository;
import com.tesis.teamsoft.persistence.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenServiceImpl {

    @Value("${app.security.password-reset.expiration-minutes:30}")
    private int expirationMinutes;

    private final IPasswordResetTokenRepository tokenRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public String createPasswordResetToken(UserEntity user) {
        // Invalidar tokens previos no usados
        tokenRepository.findByUserAndUsedFalse(user).ifPresent(token -> {
            token.setUsed(true);
            tokenRepository.save(token);
        });

        // Crear nuevo token
        String tokenValue = UUID.randomUUID().toString();
        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(expirationMinutes))
                .used(false)
                .build();

        tokenRepository.save(token);
        log.debug("Created password reset token for user ID: {}", user.getId());

        return tokenValue;
    }

    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        Optional<PasswordResetTokenEntity> resetToken = tokenRepository.findByTokenAndUsedFalse(token);
        return resetToken.isPresent() && resetToken.get().isValid();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetTokenEntity resetToken = tokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Token has expired or already used");
        }

        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        //log.info("Password reset successfully for user: {}", user.getUsername());
    }

    @Scheduled(cron = "0 0 2 * * ?") // Ejecutar cada día a las 2 AM
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        tokenRepository.deleteByExpiryDateBefore(cutoff);
        //log.debug("Cleaned up expired password reset tokens");
    }
}
