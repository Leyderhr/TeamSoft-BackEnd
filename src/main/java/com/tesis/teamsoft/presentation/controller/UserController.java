package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.presentation.dto.UserDTO;
import com.tesis.teamsoft.service.implementation.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "User Management APIs")
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping()
    @Operation(summary = "Create a new user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.UserResponseDTO> createUser(@Valid @RequestBody UserDTO.UserCreateDTO userDTO) {
            UserDTO.UserResponseDTO createdUser = userService.saveUser(userDTO);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO.UserCreateDTO userDTO) {
        UserDTO.UserResponseDTO updatedUser = userService.updateUser(userDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
            String message = userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "Get all users ordered by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO.UserResponseDTO>> getAllUsers() {
            return new ResponseEntity<>(userService.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.UserResponseDTO> getUserById(@PathVariable Long id) {
            return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPasswordToDefault(@PathVariable Long id) {
            String message = userService.resetPasswordToDefault(id);
            return ResponseEntity.ok(Map.of("message", message));
    }
}