package com.tesis.teamsoft.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AssignedRoleDTO {

    private RoleDTO.RoleMinimalDTO role;
    private List<PersonDTO.PersonMinimalDTO> persons;

    public AssignedRoleDTO(RoleDTO.RoleMinimalDTO role) {
        this.role = role;
        persons = new ArrayList<>();
    }
}
