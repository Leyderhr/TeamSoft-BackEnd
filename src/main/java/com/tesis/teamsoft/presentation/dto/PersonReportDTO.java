package com.tesis.teamsoft.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Reporte de una persona: todos sus datos y los proyectos en los que ha
 * participado, con el rol desempeñado y su evaluación en cada uno.
 */
@Data
public class PersonReportDTO {

    private PersonDTO.PersonResponseDTO person;
    private List<ParticipationDTO> participations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParticipationDTO {
        private ProjectDTO.ProjectSimpleDTO project;
        private RoleDTO.RoleMinimalDTO role;
        private String evaluation;
    }
}
