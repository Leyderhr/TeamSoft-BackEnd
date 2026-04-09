package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllPersonAssigned extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {

        List<PersonEntity> toAssign = parameters.getSearchArea(); //todas las personas que deben ser asignadas

        List<Object> projects = state.getCode();  //lista de projectos - roles

        int p = 0;
        boolean meet = true;
        while (p < toAssign.size() && meet) {  //para cada persona que debe ser asignada
            int i = 0;
            boolean next = false;
            while (i < projects.size() && !next) {  // para cada projecto-rol
                ProjectRole projectRole = (ProjectRole) projects.get(i);
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                int j = 0;
                while (j < roleWorkers.size() && !next) { //para cada rol-persona
                    RoleWorker roleWorker = roleWorkers.get(j);

                    List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                    aux.addAll(roleWorker.getWorkers());
                    aux.addAll(roleWorker.getFixedWorkers());

                    int k = 0;
                    while (k < aux.size() && !next) { // para cada persona en el rol
                        PersonEntity worker = aux.get(k);

                        if (worker.getId().equals(toAssign.get(p).getId())) { //si esta asignada la persona que se verifica pasar a la siguiente
                            next = true;
                        }
                        k++;
                    }
                    j++;
                }
                i++;
            }

            if (!next) { //si la persona verificada no se asignó a ningún proyecto falla la restricción
                meet = false;
            }
            p++;
        }
        return meet;
    }
}
