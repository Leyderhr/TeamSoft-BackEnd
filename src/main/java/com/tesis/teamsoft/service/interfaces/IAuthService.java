package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.LoginDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthService {

    LoginDTO.LoginResponseDTO login(LoginDTO.LoginRequestDTO loginDTO);

    void logout();

    void processForgotPassword(String email);

    void resetPassword(String token, String newPassword);

    void changePassword(String username, String currentPassword, String newPassword);

    LoginDTO.RefreshTokenResponseDTO refreshToken(String refreshToken);
}
