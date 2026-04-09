package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonConflictEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.util.List;
import java.util.Random;

/**
 * No permitir que en un mismo equipo se encuentren personas incompatibles
 * (incompatibilidad = 0)
 */
public class IncompatibleWorkers extends Constrain {

    @Override
    public Boolean ValidateState(State state) {

        int projects = state.getCode().size();
        int i = 0;
        boolean meet = true;

        while (i < projects && meet) {
            meet = validateProyect(state, i);
            i++;
        }
        return meet;
    }

    public boolean validateProyect(State state, int posProy) {

        List<Object> projects = state.getCode();
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

        boolean meet = true;

        int j = 0;
        while (j < roleWorkers.size() && meet) {

            RoleWorker roleWorker = roleWorkers.get(j);
            List<PersonEntity> workers = roleWorker.getWorkers();

            int k = 0;
            while (k < workers.size() && meet) {
                PersonEntity worker = workers.get(k);
                List<PersonConflictEntity> workerConflicts = worker.getPersonConflictList();

                int l = 0;
                while (l < projectWorkers.size() && meet) {
                    PersonEntity workerC = projectWorkers.get(l);

                    if (!worker.getId().equals(workerC.getId())) {
                        int m = 0;
                        while (m < workerConflicts.size() && meet) {
                            if (workerC.getId().equals(workerConflicts.get(m).getPerson().getId())
                                    || workerC.getId().equals(workerConflicts.get(m).getPersonConflict().getId())) { //workerFk
                                if (workerConflicts.get(m).getIndex().getWeight() == 1) {
                                    meet = false;
                                }
                            }
                            m++;
                        }
                    }
                    l++;
                }
                k++;
            }
            j++;
        }
        return meet;
    }


    public void repareState(State state, int posProy) {

        List<Object> projects = state.getCode();
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random generator = new Random();
        int i = 0;

        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

        int j = 0;
        while (j < roleWorkers.size()) {

            RoleWorker roleWorker = roleWorkers.get(j);
            List<PersonEntity> workers = roleWorker.getWorkers();

            int k = 0;
            while (k < workers.size()) {
                PersonEntity worker = workers.get(k);
                List<PersonConflictEntity> workerConflicts = worker.getPersonConflictList();

                int l = 0;
                while (l < projectWorkers.size()) {
                    PersonEntity workerC = projectWorkers.get(l);

                    if (!worker.getId().equals(workerC.getId())) {
                        int m = 0;
                        while (m < workerConflicts.size()) {
                            if (workerC.getId().equals(workerConflicts.get(m).getPerson().getId())
                                    || workerC.getId().equals(workerConflicts.get(m).getPersonConflict().getId())) { //workerFk
                                if (workerConflicts.get(m).getIndex().getWeight() == 1) {
                                    int personIndex = generator.nextInt(codification.getSearchArea().size());
                                    PersonEntity newWorker = codification.getSearchArea().get(personIndex);
                                    ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, newWorker);
                                }
                            }
                            m++;
                        }
                    }
                    l++;
                }
                k++;
            }
            j++;
        }
    }
}
