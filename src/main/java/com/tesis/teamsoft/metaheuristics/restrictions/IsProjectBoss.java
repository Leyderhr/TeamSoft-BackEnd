package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.AssignedRoleEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * RESTRICCIÓN PARA LA SELECCIÓN DEL JEFE DE PROYECTO Verificar que la persona
 * no sea jefe de ningun otro proyecto ( el assigned role debe tener fecha de
 * fin si el rol es jefe de proyecto )
 *
 * @author G1lb3rt
 */
public class IsProjectBoss extends Constrain {

    public IsProjectBoss() {
    }

    @Override
    public Boolean ValidateState(State state) {

        List<PersonEntity> people = new ArrayList<>();  //lista de todas las personas de la solución
        List<Object> projects = state.getCode();  //lista de personas - roles

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {  // para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            int j = 0;
            while (j < projectRole.getRoleWorkers().size() && meet) { //para cada rol-persona
                RoleWorker rolePerson = projectRole.getRoleWorkers().get(j);
                if (rolePerson.getRole().isBoss()) { //si el rol actual es Jefe de Proyecto
                    List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                    aux.addAll(rolePerson.getWorkers());
                    aux.addAll(rolePerson.getFixedWorkers());
                    int k = 0;
                    while (k < aux.size() && meet) { // para cada persona
                        PersonEntity worker = aux.get(k);
                        meet = validatePerson(people, worker);
                        k++;
                        people.add(worker);
                    }
                }
                j++;
            }
            i++;
        }
        return meet;
    }

    public boolean validatePerson(List<PersonEntity> people, PersonEntity worker) {
        boolean meet = WorkerNotRepeatedInSameRole.getWorkerOcurrences(people, worker) < 1;

        // validar que no esté asignada como jefe en un proyecto anterior
        int l = 0;
        while (l < worker.getAssignedRoleList().size() && meet) { // para cada rol asignado a la persona con anterioridad
            AssignedRoleEntity rol = worker.getAssignedRoleList().get(l);
            if (rol.getRole().isBoss()) { //si  desempeña el rol Jefe de Proyecto
                if (rol.getEndDate() != null) {  // si aun esta activo en el proyecto
                    meet = false;
                }
            }
            l++;
        }
        return meet;
    }
}
