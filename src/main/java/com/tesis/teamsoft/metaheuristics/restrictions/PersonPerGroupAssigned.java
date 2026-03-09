package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.config.AlgorithmConfig;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.pojos.PersonPerProjectAmount;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.util.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.util.ProjectRole;
import com.tesis.teamsoft.metaheuristics.util.RoleWorker;
import com.tesis.teamsoft.metaheuristics.util.TeamFormationCodification;
import problem.definition.State;

import java.util.*;

/**
 * @author Alejandro Durán
 * que solo haya la cantidad de personas q determine el usuario para el equipo de proyecto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonPerGroupAssigned extends Constrain {

    private TeamFormationParameters parameters;

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
        ArrayList<PersonPerProjectAmount> ppg = parameters.getPpg();
        boolean meet = true;
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);
        boolean find = false;
        int j = 0;
        while (!find) {
            if (ppg.get(j).getProj().getId().equals(projectRole.getProject().getId())) {
                find = true;
                if (projectWorkers.size() != ppg.get(j).getCant()) {
                    meet = false;
                }
            }
            j++;
        }
        return meet;
    }

    public void sobranPersonasEnProyecto(State state, int i, int cant) {
        ProjectRole projectRole = (ProjectRole) state.getCode().get(i);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        int j = 0, c = 0;
        PersonEntity worker = roleWorkers.getFirst().getWorkers().getFirst();
        while (j < roleWorkers.size() && c < cant) { //para cada rol-persona
            RoleWorker roleWorker = roleWorkers.get(j);
            List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
            aux.addAll(roleWorker.getWorkers());
            aux.addAll(roleWorker.getFixedWorkers());
            int k = 0;
            while (k < aux.size() && c < cant) {
                PersonEntity worker2 = aux.get(k);
                if (!Objects.equals(worker.getId(), worker2.getId())) {
                    ((ProjectRole) state.getCode().get(i)).getRoleWorkers().get(j).getWorkers().set(k, worker);
                    c++;
                }
                k++;
            }
            j++;
        }
    }

    public ArrayList<PersonEntity> personasProyecto(ProjectRole projectRole) {
        // devuelve un arreglo con las personas que hay en la solución
        ArrayList<PersonEntity> personas = new ArrayList<>();
        for (int j = 0; j < projectRole.getRoleWorkers().size(); j++) {
            RoleWorker rw = projectRole.getRoleWorkers().get(j);
            personas.addAll(rw.getWorkers());
        }
        return personas;
    }


    public void repareState(State state, int posProy) {
        List<Object> projects = state.getCode();
        ArrayList<PersonPerProjectAmount> ppg = parameters.getPpg();
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);
        boolean find = false;
        int j = 0;
        while (!find) {
            if (ppg.get(j).getProj().getId().equals(projectRole.getProject().getId())) {
                find = true;
                int cant = Math.abs(projectWorkers.size() - ppg.get(j).getCant());
                if (projectWorkers.size() > ppg.get(j).getCant()) {
                    sobranPersonasEnProyecto(state, posProy, cant);
                } else if (projectWorkers.size() < ppg.get(j).getCant()) {
                    faltanPersonasEnProyecto(state, posProy, cant);
                }
            }
            j++;
        }
    }

    public void faltanPersonasEnProyecto(State state, int posProy, int cant) {
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random random = new Random();
        ProjectRole projectRole = (ProjectRole) state.getCode().get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        List<PersonEntity> projectWorkers = personasProyecto(projectRole);
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);
        if (cantIntentos < cant)
            cantIntentos = cant;
        int j = 0, c = 0;
        while (j < roleWorkers.size() && c < cant) { //para cada rol-persona
            RoleWorker roleWorker = roleWorkers.get(j);
            List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
            aux.addAll(roleWorker.getWorkers());
            aux.addAll(roleWorker.getFixedWorkers());

            int k = 0;
            while (k < aux.size() && c < cant) {
                PersonEntity worker = aux.get(k);
                if (WorkerNotRepeatedInSameRole.getWorkerOcurrences(projectWorkers, worker) > 1) { //cambia una persona que  está repetida

                    int indexP2 = random.nextInt(codification.getSearchArea().size());
                    PersonEntity worker2 = codification.getSearchArea().get(indexP2);
                    int in = 0;

                    while (WorkerNotRepeatedInSameRole.getWorkerOcurrences(projectWorkers, worker2) >= 1 && in < cantIntentos) {
                        indexP2 = random.nextInt(codification.getSearchArea().size());
                        worker2 = codification.getSearchArea().get(indexP2);
                        in++;
                    }
                    ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, worker2);
                    c++;
                    projectWorkers = personasProyecto((ProjectRole) state.getCode().get(posProy));

                }

                k++;
            }
            j++;
        }
        while (c < cant) {
            int indexR = random.nextInt(roleWorkers.size());
            int indexP = random.nextInt(codification.getSearchArea().size());
            PersonEntity worker2 = codification.getSearchArea().get(indexP);
            int in = 0;
            while (WorkerNotRepeatedInSameRole.getWorkerOcurrences(projectWorkers, worker2) >= 1 && in < cantIntentos) {
                indexP = random.nextInt(codification.getSearchArea().size());
                worker2 = codification.getSearchArea().get(indexP);
                in++;
            }
            ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(indexR).getWorkers().add(worker2);
            c++;
            projectWorkers = personasProyecto((ProjectRole) state.getCode().get(posProy));

        }

    }

}
