package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonTestEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

//es IS o CO  ; para jefe de proyecto
public class IsISCO extends Constrain {

    public IsISCO() {
    }

    @Override
    public Boolean ValidateState(State state) {
        List<Object> projects = state.getCode(); //lista de proyectos  roles
        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) { // para cada proyecto
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            int j = 0;
            while (j < roleWorkers.size() && meet) { //para cada rol
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                if (roleWorker.getRole().isBoss()) { //si el rol actual es Jefe de proyecto
                    int k = 0;
                    while (k < aux.size() && meet) { // para cada persona
                        PersonEntity worker = aux.get(k);
                        meet = validatePerson(worker);
                        k++;
                    }
                }
                j++;
            }
            i++;
        }
        return meet;
    }


    public boolean validatePerson(PersonEntity worker) {
        boolean meet = true;
        PersonTestEntity workerTest = (PersonTestEntity) worker.getPersonTest();

        if (workerTest != null) { //si se registraron sus caracteristicas psicologicas
            if ((workerTest.getCO() == 'I' || workerTest.getCO() == 'E') && (workerTest.getIS() == 'I' || workerTest.getIS() == 'E')) {
                meet = false;
            }
        } else {  //si no se cuenta con los datos psicologicos de la persona
            meet = false;
        }
        return meet;
    }

}
