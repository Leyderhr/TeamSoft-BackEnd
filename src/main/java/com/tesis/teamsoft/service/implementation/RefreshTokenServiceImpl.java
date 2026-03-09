package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.RefreshTokenEntity;
import com.tesis.teamsoft.persistence.entity.UserEntity;
import com.tesis.teamsoft.persistence.repository.IRefreshTokenRepository;
import com.tesis.teamsoft.persistence.repository.IUserRepository;
import com.tesis.teamsoft.service.interfaces.IRefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Value("${security.jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional
    public RefreshTokenEntity createRefreshToken(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {}", username);
        
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            log.warn("Refresh token expired and deleted: {}", token.getToken());
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        if (token.getRevokedAt() != null) {
            log.warn("Attempt to use revoked refresh token: {}", token.getToken());
            throw new RuntimeException("Refresh token has been revoked. Please login again.");
        }

        return token;
    }

    @Override
    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteByUser(user);
        log.info("All refresh tokens deleted for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeToken(token, Instant.now());
        log.info("Refresh token revoked: {}", token);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
        log.info("Expired refresh tokens cleaned up");
    }
}
