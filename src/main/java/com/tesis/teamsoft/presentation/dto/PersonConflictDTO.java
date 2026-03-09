package com.tesis.teamsoft.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonConflictDTO {

    @Data
    public static class PersonConflictCreateDTO {
        @NotNull(message = "Conflict index ID is required")
        private Long conflictIndexId;

        @NotNull(message = "Person conflict ID is required")
        private Long personConflictId;
    }

    @Data
    public static class PersonConflictResponseDTO {
        private Long id;
        private ConflictIndexDTO.ConflictIndexResponseDTO conflictIndex;
        private PersonDTO.PersonMinimalDTO personConflict;
    }
}
