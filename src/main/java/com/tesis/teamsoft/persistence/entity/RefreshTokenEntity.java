package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant revokedAt;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
