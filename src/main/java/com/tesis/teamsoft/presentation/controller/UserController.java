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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO.UserCreateDTO userDTO,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildValidationErrors(bindingResult);
        }

        try {
            UserDTO.UserResponseDTO createdUser = userService.saveUser(userDTO);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PutMapping("/{id}")
//    @Operation(summary = "Update an existing user")
//    public ResponseEntity<?> updateUser(@PathVariable Long id,
//                                        @Valid @RequestBody UserDTO.UserUpdateDTO userDTO,
//                                        BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return buildValidationErrors(bindingResult);
//        }
//
//        try {
//            UserDTO.UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
//            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            if (e.getMessage().contains("not found")) {
//                return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
//            }
//            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            String message = userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    @Operation(summary = "Get all users ordered by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return new ResponseEntity<>(userService.findAllByOrderByIdAsc(), HttpStatus.OK);
        } catch (Exception e) {
            return buildErrorResponse("Error retrieving users", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> buildValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, status);
    }
}