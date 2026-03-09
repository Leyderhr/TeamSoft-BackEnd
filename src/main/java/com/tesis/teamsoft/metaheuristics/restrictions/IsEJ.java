package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.metaheuristics.util.ProjectRole;
import com.tesis.teamsoft.metaheuristics.util.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

//es E y J  para jefe de proyecto
public class IsEJ extends Constrain {

    public IsEJ() {
    }

    @Override
    public Boolean ValidateState(State state) {
        List<Object> projects = state.getCode(); //lista de proyectos
        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) { //para cada proyecto -rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            int j = 0;
            while (j < roleWorkers.size() && meet) { //para cada rol trabajador
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                if (roleWorker.getRole().isBoss()) { // si el rol actual es jefe de proyecto
                    int k = 0;
                    while (k < aux.size() && meet) { //para cada persona
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
        String mb = "";
        String E = "";
        String J = "";

        if (worker.getPersonTest() != null) {   // si se registraron características psicológicas para la misma
            mb = worker.getPersonTest().getMbtiType();
            E = mb.substring(0, 1);
            J = mb.substring(3, 4);
            if (!E.equalsIgnoreCase("e") || !J.equalsIgnoreCase("j")) { // si no tiene el subtipo E__J
                meet = false;
            }
        } else {  //si no se cuenta con los datos psicologicos de la persona
            meet = false;
        }
        return meet;
    }
}
