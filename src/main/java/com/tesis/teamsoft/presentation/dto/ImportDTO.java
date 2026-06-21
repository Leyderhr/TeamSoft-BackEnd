package com.tesis.teamsoft.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTOs del Asistente de Importación de Personas desde CSV (flujo stateless en 2 pasos).
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportDTO {

    /** Respuesta de POST /import/parse. */
    @Data
    public static class ParseResponseDTO {
        private String fileId;
        private List<String> headers;
        private List<Map<String, String>> preview;
    }

    /** Petición de POST /import/execute. */
    @Data
    public static class ExecuteRequestDTO {
        @NotBlank(message = "ERR_IMPORT_FILE_ID_REQUIRED")
        private String fileId;

        @NotBlank(message = "ERR_IMPORT_GROUP_NAME_REQUIRED")
        private String groupName;

        private boolean updateIfExist;
        private boolean deleteOldValues;

        @NotNull(message = "ERR_IMPORT_CUTOFF_REQUIRED")
        private Float puntoCorte;

        @NotNull(message = "ERR_IMPORT_MAX_EXP_REQUIRED")
        private Integer maxExpValue;

        @NotNull(message = "ERR_IMPORT_PERSON_MAPPING_REQUIRED")
        @Valid
        private PersonMappingDTO personMapping;

        @Valid
        private List<CompetenceMappingDTO> competenceMapping;

        @Valid
        private List<RoleMappingDTO> roleMapping;
    }

    @Data
    public static class PersonMappingDTO {
        @NotBlank(message = "ERR_IMPORT_NAME_COLUMN_REQUIRED")
        private String nombreColumn;

        @NotBlank(message = "ERR_IMPORT_EXPERIENCE_COLUMN_REQUIRED")
        private String experienceColumn;
    }

    @Data
    public static class CompetenceMappingDTO {
        @NotBlank(message = "ERR_IMPORT_COMPETENCE_NAME_REQUIRED")
        private String competenceName;

        @NotNull(message = "ERR_IMPORT_ATTRIBUTES_REQUIRED")
        @Valid
        private List<AttributeDTO> attributes;
    }

    @Data
    public static class AttributeDTO {
        @NotBlank(message = "ERR_IMPORT_CSV_COLUMN_REQUIRED")
        private String csvColumn;

        @NotNull(message = "ERR_IMPORT_WEIGHT_REQUIRED")
        private Float weight;

        @JsonProperty("isNumeric")
        private boolean numeric;

        /** Solo para columnas de texto: peso por cada valor textual (p. ej. {"Alto":1.0,"Bajo":0.1}). */
        private Map<String, Float> textValueWeights;
    }

    @Data
    public static class RoleMappingDTO {
        @NotNull(message = "ERR_IMPORT_ROLE_ID_REQUIRED")
        private Long roleId;

        @NotBlank(message = "ERR_IMPORT_CSV_COLUMN_REQUIRED")
        private String csvColumn;
    }
}
