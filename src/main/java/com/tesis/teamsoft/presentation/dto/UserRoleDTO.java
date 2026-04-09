package com.tesis.teamsoft.presentation.dto;

import com.tesis.teamsoft.persistence.entity.auxiliary.Roles;
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
