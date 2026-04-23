package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.UserRoleDTO;
import com.tesis.teamsoft.service.interfaces.IUserRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "UserRoles")
@RequestMapping("/user-roles")
public class UserRoleController {

    private final IUserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserRoleDTO.UserRoleResponseDTO>> getAllRoles() {
        List<UserRoleDTO.UserRoleResponseDTO> roles = userRoleService.findAllByOrderByIdAsc();
        return ResponseEntity.ok(roles);
    }
}