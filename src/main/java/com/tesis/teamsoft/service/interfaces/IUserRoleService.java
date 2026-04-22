package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.RoleDTO;
import com.tesis.teamsoft.presentation.dto.UserRoleDTO;

import java.util.List;

public interface IUserRoleService {

    List<UserRoleDTO.UserRoleResponseDTO> findAllByOrderByIdAsc();
}
