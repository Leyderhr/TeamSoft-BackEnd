package com.tesis.teamsoft.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultDTO {
    private int created;
    private int updated;
    private int skipped;
    private int errors;
    private List<RowError> errorMessages = new ArrayList<>();

    /**
     * Error de una fila concreta. Se devuelve el código y sus parámetros para que el
     * frontend muestre el mensaje internacionalizado (errors.&lt;errorCode&gt;).
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RowError {
        private String row;
        private String errorCode;
        private List<Object> parameters;
    }
}
