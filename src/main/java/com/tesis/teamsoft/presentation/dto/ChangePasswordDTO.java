package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "ERR_VAL_CHANGE_PASSWORD_CURRENT_REQUIRED")
    private String currentPassword;

    @NotBlank(message = "ERR_VAL_CHANGE_PASSWORD_NEW")
    @Size(min = 6, message = "ERR_VAL_CHANGE_PASSWORD_NEW")
    private String newPassword;

    @NotBlank(message = "ERR_VAL_CHANGE_PASSWORD_CONFIRM_REQUIRED")
    private String confirmPassword;
}