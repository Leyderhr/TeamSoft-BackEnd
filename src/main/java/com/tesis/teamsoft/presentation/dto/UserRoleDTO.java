package com.tesis.teamsoft.presentation.dto;


import com.tesis.teamsoft.persistence.entity.auxiliar.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRoleDTO {

    @Data
    public static class UserRoleResponseDTO {
        private Long id;
        private Roles name;
    }
}
