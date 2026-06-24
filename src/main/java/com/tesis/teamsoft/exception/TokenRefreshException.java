package com.tesis.teamsoft.exception;

import lombok.Getter;

@Getter
public class TokenRefreshException extends RuntimeException {

    private final transient Object[] parameters;

    public TokenRefreshException(String errorCode, Object... parameters) {
        super(errorCode);
        this.parameters = parameters != null ? parameters : new Object[0];
    }

}