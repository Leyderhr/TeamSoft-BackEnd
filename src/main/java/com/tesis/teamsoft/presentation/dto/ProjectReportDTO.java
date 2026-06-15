package com.tesis.teamsoft.presentation.dto;

import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Reporte de un equipo/proyecto finalizado o cerrado: datos del proyecto y
 * los miembros que trabajaron en él con su rol y evaluación.
 */
@Data
public class ProjectReportDTO {

    private Long id;
    private String projectName;
    private Date initialDate;
    private Date endDate;
    private ProjectState state;
    private List<MemberDTO> members;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberDTO {
        private PersonDTO.PersonMinimalDTO person;
        private RoleDTO.RoleMinimalDTO role;
        private String evaluation;
    }
}
