package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.RefreshTokenEntity;
import com.tesis.teamsoft.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user = :user")
    void deleteByUser(UserEntity user);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(Instant now);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revokedAt = :now WHERE rt.token = :token")
    void revokeToken(String token, Instant now);

    boolean existsByTokenAndRevokedAtIsNull(String token);
}
