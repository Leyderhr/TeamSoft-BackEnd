package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.CycleEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.ProjectRolesEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
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
 * La carga de trabajo de un trabajador no puede ser mayor que la definida en la
 * UI ( worload del trabajador mas carga de cada rol asignado en la solucion
 * debe ser menor que la definida)
 *
 * @author G1lb3rt
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaxWorkload
        extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {
        float maxWorkload = parameters.getMaxRoleLoad().getValue(); //obtener restricción para carga de trabajo
        List<Object> projects = state.getCode(); //lista de proyectos -roles
        List<PersonEntity> people = new ArrayList<>(); // personas de la solución
        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) { //para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            int j = 0;
            while (j < roleWorkers.size() && meet) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                float rolLoad = findWorkLoad(roleWorker.getRole(), projectRole.lastProjectCycle()); //carga de trabajo que implica desempeñar el rol en el proyecto actual
                int k = 0;
                while (k < aux.size() && meet) { //por cada persona en el rol
                    PersonEntity worker = new PersonEntity();
                    worker.setWorkload(aux.get(k).getWorkload());
                    worker.setId(aux.get(k).getId());
                    meet = validatePerson(worker, people, rolLoad, maxWorkload);
                    k++;
                }
                j++;
            }
            i++;
        }
        return meet;
    }

    public boolean validatePerson(PersonEntity worker, List<PersonEntity> people, float rolLoad, float maxWorkload) {
        int l = 0;
        boolean meet = true;
        if (!people.isEmpty()) {
            boolean found = false;
            while (l < people.size() && !found) { // recorrer lista de personas
                if (people.get(l).getId().equals(worker.getId())) {  //si ya se encuentra desempeñando otro rol (es decir si esta en la lista)
                    people.get(l).setWorkload(people.get(l).getWorkload() + rolLoad); //aumentar carga de trabajo
                    if (people.get(l).getWorkload() > maxWorkload) { //si sobrepasa la carga definida
                        meet = false;
                    }
                    found = true;
                }
                l++;
            }

            if (!found) { //si la persona no se encuentra desempeñando otro rol (no esta en la lista)
                if (worker.getWorkload() == null) {
                    worker.setWorkload(0f);
                }
                worker.setWorkload(worker.getWorkload() + rolLoad); //actualizar carga de trabajo de la persona
                if (worker.getWorkload() > maxWorkload) { //si sobrepasa la carga definida
                    meet = false;
                } else {
                    people.add(worker);
                }
            }
        }
        return meet;
    }

    /**
     * Carga de trabajo que implica desempñar un rol en un projecto dado
     */
    public float findWorkLoad(RoleEntity role, CycleEntity lastProjectCycle) {
        float rolLoad = 0;
        List<ProjectRolesEntity> roles = lastProjectCycle.getProjectStructure().getProjectRolesList();
        int i = 0;
        boolean found = false;
        while (i < roles.size() && !found) {
            if (roles.get(i).getRole().getId().equals(role.getId())) {
                rolLoad = roles.get(i).getRoleLoad().getValue();
                found = true;
            }
            i++;
        }
        return rolLoad;
    }
}
