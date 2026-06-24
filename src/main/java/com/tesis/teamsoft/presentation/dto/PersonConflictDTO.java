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
        @NotNull(message = "ERR_VAL_PERSON_CONFLICT_INDEX_ID")
        private Long conflictIndexId;

        @NotNull(message = "ERR_VAL_PERSON_CONFLICT_TARGET_ID")
        private Long personConflictId;
    }

    @Data
    public static class PersonConflictResponseDTO {
        private Long id;
        private ConflictIndexDTO.ConflictIndexResponseDTO conflictIndex;
        private PersonDTO.PersonMinimalDTO personConflict;
    }
}
