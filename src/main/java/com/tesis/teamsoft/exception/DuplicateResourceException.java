package com.tesis.teamsoft.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final transient Object[] parameters;

    public DuplicateResourceException(String errorCode, Object... parameters) {
        super(errorCode);
        this.parameters = parameters != null ? parameters : new Object[0];
    }

}