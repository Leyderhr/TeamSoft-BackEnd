package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonTestEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Que en el equipo existan la cantidad de personas con el rol cerebro definidas
 * en la GUI, 1 por defecto.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExistCerebro extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {

        int min = parameters.getCountBrains();

        int personCount = 0; //contador de personas con rol cerebro
        List<Object> projects = state.getCode();  //lista de projectos - roles

        int i = 0;
        boolean meet = false;
        while (i < projects.size() && !meet) {  // para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            int j = 0;
            while (j < roleWorkers.size() && !meet) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);

                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());

                int k = 0;
                while (k < aux.size() && !meet) { // para cada persona
                    PersonEntity worker = aux.get(k);

                    if (worker.getPersonTest() != null) {
                        if (worker.getPersonTest().getCE() != 'I' && worker.getPersonTest().getCE() != 'E') { // si tiene rol cerebro
                            personCount++; //cuento uno
                        }
                        if (personCount >= min) { //si esta cubierta la cantidad definida por el usuario
                            meet = true;
                        }
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
        return meet;
    }


    public void repareState(State state, int cantFaltan) {

        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        List<Object> projects = state.getCode();  //lista de projectos - roles

        ArrayList<PersonEntity> candidatos = new ArrayList<>();

        int i = 0;
        while (i < codification.getSearchArea().size() && (candidatos.size() < cantFaltan)) {

            PersonEntity worker = codification.getSearchArea().get(i);
            PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas

            if (workerTest.getCE() != 'I' && workerTest.getCE() != 'E') {
                candidatos.add(worker);
            }

        }

        int c = 0;
        int x = 0;
        while (x < projects.size() && !candidatos.isEmpty()) {
            ProjectRole projectRole = (ProjectRole) projects.get(x);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            int j = 0;
            while (j < roleWorkers.size() && !candidatos.isEmpty()) {
                RoleWorker rw = roleWorkers.get(j);
                int k = 0;
                while (k < rw.getWorkers().size()) {
                    PersonEntity worker = rw.getWorkers().get(k);
                    if (!(worker.getPersonTest().getCE() != 'I' && worker.getPersonTest().getCE() != 'E')) { // si tiene rol cerebro
                        rw.getWorkers().set(k, candidatos.remove(c));
                        c++;
                    }
                    k++;
                }
                j++;
            }
            x++;
        }
    }
}
