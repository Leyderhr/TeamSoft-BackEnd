package com.tesis.teamsoft.presentation.controller;

import com.tesis.teamsoft.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.tesis.teamsoft.presentation.controller")
public class GlobalExceptionHandler {

    /**
     * MÉTODO CENTRALIZADO (Helper)
     * Construye la estructura exacta del JSON de error requerido para el Enfoque 1.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String errorCode, Object parameters) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("errorCode", errorCode);
        response.put("parameters", parameters != null ? parameters : new Object[0]);
        response.put("timestamp", LocalDateTime.now()); // Jackson lo serializará en formato ISO

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        // Enviamos el mapa de errores en los campos dentro de la propiedad 'parameters'
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", errorMessages);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        List<String> errors = ex.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        // ex.getMessage() contiene el código abstract (ej: "ERR_COMP_IMPORTANCE_NOT_FOUND")
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getParameters());
    }

    @ExceptionHandler({BusinessRuleException.class, DuplicateResourceException.class, IllegalArgumentException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleBusinessRule(RuntimeException ex) {
        String errorCode = ex.getMessage();
        Object[] parameters;

        switch (ex) {
            case BusinessRuleException e ->
                parameters = e.getParameters();
            case DuplicateResourceException e ->
                parameters = e.getParameters();

            default -> {
                errorCode = "ERR_DATA_INTEGRITY_OR_ARGUMENT";
                parameters = new Object[]{ex.getMessage()};
            }
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorCode, parameters);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Map<String, Object>> handleTokenRefresh(TokenRefreshException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getParameters());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        String details = ex.getMostSpecificCause().getMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "ERR_MALFORMED_JSON", new Object[]{details});
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "ERR_INVALID_CREDENTIALS", new Object[0]);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "ERR_AUTHENTICATION_FAILED", new Object[]{ex.getMessage()});
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "ERR_ACCESS_DENIED", new Object[]{"You don't have permission to access this resource"});
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_INTERNAL_SERVER_ERROR", new Object[0]);
    }
}