package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.AssignedRoleEntity;
import com.tesis.teamsoft.persistence.entity.CycleEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Verificar que el trabajador no este activo en un ciclo anterior del proyecto
 * actual
 *
 * @author G1lb3rt
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerRemovedFromRole extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {

        List<Object> projects = state.getCode();  //lista de proyectos - roles

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {  // para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            List<CycleEntity> cycleList = projectRole.getProject().getCycleList();   //obtener ciclos del proyeccto actual

            int j = 0;
            while (j < roleWorkers.size() && meet) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);

                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());

                int k = 0;
                while (k < aux.size() && meet) { // para cada persona
                    PersonEntity worker = aux.get(k);

                    meet = validatePerson(worker, roleWorker, cycleList);
                    k++;
                }
                j++;
            }
            i++;
        }
        return meet;
    }

    public boolean validatePerson(PersonEntity worker, RoleWorker roleWorker, List<CycleEntity> cycleList) {
        boolean meet = true;

        int l = 0;
        while (l < cycleList.size() && meet) { // para cada ciclo

            List<AssignedRoleEntity> assignedRoleList = cycleList.get(l).getAssignedRoleList(); //obtener lista de roles asignados al ciclo

            int m = 0;
            while (m < assignedRoleList.size() && meet) { // para cada rol asignado al ciclo
                if (assignedRoleList.get(m).getStatus().equals("ACTIVE") && assignedRoleList.get(m).getPerson().getId().equals(worker.getId()) && assignedRoleList.get(m).getRole().getId().equals(roleWorker.getRole().getId())) {
                    meet = false;
                }
                m++;
            }
            l++;
        }

        return meet;
    }

}
