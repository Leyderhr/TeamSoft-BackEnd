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

import java.util.List;

/**
 * Que una persona no pueda desempeñar menos roles que los definidos en la
 * interfaz EN TODOS LOS PROYECTOS
 *
 * @author jpinas
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinimumRoles extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {
        int minRoles = parameters.getMinimumRole(); //cant. min. de roles definida por el usuario
        List<Object> projects = state.getCode();  //lista de proyectos - roles
        boolean cumple = true;

        // por cada proyecto
        for (Object pr : projects) {
            ProjectRole projectRole = (ProjectRole) pr;
            for (RoleWorker roleWorker : projectRole.getRoleWorkers()) { // por cada rol
                for (PersonEntity worker : roleWorker.getWorkers()) { // por cada trabajador
                    /*si la cant de roles asignadas en el proyecto es menor que
                     * la cantidad de roles minima establecida por el usuario entonces
                     * no es válida la solución*/
                    if (projectRole.getCantOccurenceWorker(worker) < minRoles) {
                        cumple = false;
                        break;
                    }
                }
                if (!cumple) break;
            }
            if (!cumple) break;
        }
        return cumple;
    }
}
