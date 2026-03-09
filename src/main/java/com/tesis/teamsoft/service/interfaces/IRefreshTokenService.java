package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.persistence.entity.RefreshTokenEntity;

public interface IRefreshTokenService {
    
    RefreshTokenEntity createRefreshToken(String username);
    
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    
    RefreshTokenEntity findByToken(String token);
    
    void deleteByUserId(Long userId);
    
    void revokeToken(String token);
    
    void deleteExpiredTokens();
}
