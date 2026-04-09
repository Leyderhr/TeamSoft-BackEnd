package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.AssignedRoleEntity;
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
 * Que una persona no pueda desempeñar más roles que los definidos en la
 * interfaz (1 por defecto) (esta cantidad es en general no por proyecto)
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaximumRoles extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {
        List<PersonEntity> people = new ArrayList<>();  //lista de personas por proyecto
        List<Object> projects = state.getCode();  //lista de proyectos - roles
        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {  // para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            int j = 0;
            while (j < projectRole.getRoleWorkers().size() && meet) { //para cada rol-persona
                RoleWorker rolePerson = projectRole.getRoleWorkers().get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(rolePerson.getWorkers());
                aux.addAll(rolePerson.getFixedWorkers());
                people.addAll(rolePerson.getWorkers()); //añadir personas a lista de personas;
                int k = 0;
                while (k < aux.size() && meet) { // para cada persona
                    PersonEntity worker = aux.get(k);
                    meet = validatePerson(worker, people);
                    k++;
                }
                j++;
            }
            i++;
        }
        return meet;
    }


    public boolean validatePerson(PersonEntity worker, List<PersonEntity> people) {
        // people -> lista de personas presentes en el proyecto
        boolean meet = true;
        int maxRoles = parameters.getMaximunRoles(); //cant. max.  de roles definida por el usuario
        List<AssignedRoleEntity> assignedRoles = worker.getAssignedRoleList(); //obtener roles a los que fue asignada con anterioridad.
        int rolesCount = WorkerNotRepeatedInSameRole.getWorkerOcurrences(people, worker); //obtener roles que juega en la solucion actual
        if (rolesCount > maxRoles) {// si el número de roles propuestos en la solucion actual excede el definido
            meet = false;
        }
        int l = 0;
        while (l < assignedRoles.size() && meet) { //para cada rol asignado anteriormente
            if (assignedRoles.get(l).getStatus().equals(Status.ACTIVE)) { // si la persona aun se encuentra desempeñando este rol
                rolesCount++;
                if (rolesCount > maxRoles) { //si la cantidad de roles desempeñados exede el definodo
                    meet = false;
                }
            }
            l++;
        }
        return meet;
    }
}
