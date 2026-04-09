package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonConflictEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.pojos.SelectedWorkerIncompatibility;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.util.List;
import java.util.Random;

/**
 * No peritir las incompatibilidades seleccionadas en la GUI, en un equipo
 * determinada
 */
@Getter
@Setter
@AllArgsConstructor
public class IncompatibleSelectedWorkers extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {

        List<Object> projects = state.getCode();

        int i = 0;
        boolean meet = true;

        while (i < projects.size() && meet) {
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

            meet = validateProyect(state, i);
            i++;

            for (PersonEntity projectWorker : projectWorkers) {
                System.out.println(projectWorker.getPersonName() + "\n");
            }
        }
        return meet;
    }

    public boolean validateProyect(State state, int posProy) {
        List<SelectedWorkerIncompatibility> wiL = parameters.getSWI();
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

                int m = 0;
                while (m < wiL.size() && meet) {
                    SelectedWorkerIncompatibility swI = wiL.get(m);
                    PersonEntity workerA = swI.getWorkerAFk();
                    PersonEntity workerB = swI.getWorkerBFk();

                    if (workerA.getId().equals(worker.getId())) {
                        int l = 0;
                        while (l < projectWorkers.size() && meet) {
                            PersonEntity workerC = projectWorkers.get(l);
                            if (workerC.getId().equals(workerB.getId())) {
                                if (workerConflicts.get(m).getIndex().getWeight() == 1) {
                                    meet = false;
                                }
                            }
                            l++;
                        }
                    }

                    //Segunda Variante
                    if (workerB.getId().equals(worker.getId())) {
                        int l = 0;
                        while (l < projectWorkers.size() && meet) {
                            PersonEntity workerC = projectWorkers.get(l);
                            if (workerC.getId().equals(workerA.getId())) {
                                if (workerConflicts.get(m).getIndex().getWeight() == 1) {
                                    meet = false;
                                }
                            }
                            l++;
                        }
                    }
                    m++;
                }
                k++;
            }
            j++;
        }
        return meet;
    }


    public void invalidateState(State state) {

        List<SelectedWorkerIncompatibility> wiL = parameters.getSWI();
        SelectedWorkerIncompatibility swI = wiL.get(0);
        PersonEntity workerA = swI.getWorkerAFk();
        PersonEntity workerB = swI.getWorkerBFk();

        ((ProjectRole) state.getCode().getFirst()).getRoleWorkers().get(0).getWorkers().set(0, workerA);
        ((ProjectRole) state.getCode().getFirst()).getRoleWorkers().get(1).getWorkers().set(0, workerB);

    }


    public void repareState(State state, int posProy) {
        List<SelectedWorkerIncompatibility> wiL = parameters.getSWI();
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random generator = new Random();

        List<Object> projects = state.getCode();

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

                int m = 0;
                while (m < wiL.size()) {
                    SelectedWorkerIncompatibility swI = wiL.get(m);
                    PersonEntity workerA = swI.getWorkerAFk();
                    PersonEntity workerB = swI.getWorkerBFk();

                    if (workerA.getId().equals(worker.getId())) {
                        int l = 0;
                        while (l < projectWorkers.size()) {
                            PersonEntity workerC = projectWorkers.get(l);
                            if (workerC.getId().equals(workerB.getId())) {
                                if (workerConflicts.get(m).getIndex().getWeight() == 1) {
                                    int personIndex = generator.nextInt(codification.getSearchArea().size());
                                    PersonEntity newWorker = codification.getSearchArea().get(personIndex);
                                    ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, newWorker);
                                }
                            }
                            l++;
                        }
                    }
                    m++;
                }
                k++;
            }
            j++;
        }
    }
}
