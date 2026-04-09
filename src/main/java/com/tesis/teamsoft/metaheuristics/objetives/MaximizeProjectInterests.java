package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonalProjectInterestsEntity;
import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lesmes
 */
public class MaximizeProjectInterests extends ObjetiveFunction {

    public static String className = "ProjectIntereses";

    @Override
    public Double Evaluation(State state) {
        double total_interest = 0;
        double projectInterest;
        double normalization;
        double maxPerson = 0;
        double projectPersons;

        ArrayList<PersonEntity> projectWorkerList = new ArrayList<>();
        List<Object> projects = state.getCode();

        for (Object item : projects) {
            ProjectRole projectRole = (ProjectRole) item;
            ProjectEntity project = projectRole.getProject();

            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            projectInterest = 0;

            for (RoleWorker roleWorker : roleWorkers) {
                List<PersonEntity> workers = roleWorker.getWorkers();
                for (PersonEntity worker : workers) {
                    if (addWorker(projectWorkerList, worker)) {
                        List<PersonalProjectInterestsEntity> personalProjectInterests = worker.getPersonalProjectInterestsList();
                        for (PersonalProjectInterestsEntity ppi : personalProjectInterests) {
                            if (ppi.getProject().getProjectName().equalsIgnoreCase(project.getProjectName())) {
                                if (ppi.isPreference()) {
                                    projectInterest = projectInterest + 1;
                                }
                            }
                        }
                    }
                }
            }
            projectPersons = projectWorkerList.size();
            projectWorkerList.clear();
            total_interest = total_interest + projectInterest;
            maxPerson = maxPerson + projectPersons;
        }

        System.out.println("objective.function.MaximizeProjectInterests.Evaluation()");
        System.out.println("Total Propuesta = " + total_interest + "\n"); //Eliminar
        System.out.println("Maximo Posible = " + maxPerson + "\n"); //Eliminar
        double aux = maxPerson - total_interest;
        System.out.println("Aux = " + aux + "\n"); //Eliminar
        double pepe = aux / maxPerson;
        System.out.println("pepe = " + pepe + "\n"); //Eliminar
        normalization = pepe;
        System.out.println("Indice Propuesta = " + normalization + "\n"); //Eliminar
        return normalization;
    }

    private boolean addWorker(List<PersonEntity> workersList, PersonEntity worker) {
        boolean added = false;
        boolean found = false;
        for (PersonEntity w : workersList) {
            if (w.getId().equals(worker.getId())) {
                found = true;
                break;
            }
        }
        if (!found) {
            workersList.add(worker);
            added = true;
        }
        return added;
    }

}
