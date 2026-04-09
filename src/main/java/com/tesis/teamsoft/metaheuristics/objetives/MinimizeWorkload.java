package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.ProjectRolesEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MinimizeWorkload extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "CargaTrabajo";

    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();
        List<Double> indexList = new ArrayList<>();
        float newLoad = 0;
        double balancedLoad = 0;
        int total_projects = state.getCode().size();
        double workerLoad;
        List<PersonEntity> projectWorkers = AllProjectWorkers(projects);
        double maxWorkload = maxWorkload();

        for (PersonEntity worker : projectWorkers) {
            workerLoad = worker.getWorkload();

            for (int k = 0; k < total_projects; k++) {
                ProjectRole projectRole = (ProjectRole) projects.get(k);
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                for (RoleWorker rwItem : roleWorkers) {
                    RoleEntity role = rwItem.getRole();

                    if (rwItem.getWorkers().contains(worker)) {
                        for (int l = 0; l < rwItem.getWorkers().size(); l++) {
                            workerLoad += getRoleLoad(role, projectRole);
                            newLoad += getRoleLoad(role, projectRole);
                        }
                    }

                    if (rwItem.getFixedWorkers().contains(worker)) {
                        for (int l = 0; l < rwItem.getFixedWorkers().size(); l++) {
                            workerLoad += getRoleLoad(role, projectRole);
                            newLoad += getRoleLoad(role, projectRole);
                        }
                    }
                }
                projectRole.getProjectEvaluation()[2] = workerLoad;
            }
            indexList.add(workerLoad);
        }
        double averageWorkload = averageWorkload(newLoad);

        for (Double aDouble : indexList) {
            double diference = aDouble - averageWorkload;
            double coeficient = Math.pow(diference, 2);
            balancedLoad += coeficient;
        }

        balancedLoad = (balancedLoad) / (maxWorkload);
        return balancedLoad;
    }

    public float getRoleLoad(RoleEntity role, ProjectRole pr) {
        int cycleSize = pr.getProject().getCycleList().size();
        List<ProjectRolesEntity> pRolesList = pr.getProject().getCycleList().get(cycleSize - 1).getProjectStructure().getProjectRolesList();
        float roleLoad = -1; //cambie esto betty

        boolean found = false;
        int i = 0;
        while (!found && i < pRolesList.size()) {
            if (pRolesList.get(i).getRole().getId().equals(role.getId())) {
                roleLoad = pRolesList.get(i).getRoleLoad().getValue();
                found = true;
            } else {
                i++;
            }
        }
        return roleLoad;
    }

    public double averageWorkload(float newLoad) {
        List<PersonEntity> workers = new ArrayList<>(((TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification()).getSearchArea());
        float actualLoad = 0;
        float totalLoad;
        float averageWorkload;

        for (PersonEntity worker : workers) {
            actualLoad += worker.getWorkload();
        }
        totalLoad = actualLoad + newLoad;
        averageWorkload = totalLoad / workers.size();
        return averageWorkload;
    }

    public double maxWorkload() {
        List<PersonEntity> workers = new ArrayList<>(((TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification()).getSearchArea());
        float totalLoad = parameters.getMaxRoleLoad().getValue();
        double averageLoad;
        double actualLoad = 0;
        double maxWorkload = 0;
        List<Float> workerSumMax = new ArrayList<>();
        
        int amount_workers_max;
        if(workers.size()%2 == 0){ //Si la cantidad de proyectos es par
            amount_workers_max = workers.size()/2;
        }
        else{
            amount_workers_max = (workers.size()+1)/2;
        }
        for (PersonEntity ignored : workers) {
            if (amount_workers_max > 0) {
                workerSumMax.add(totalLoad);
                actualLoad += totalLoad;
                amount_workers_max--;
            } else {
                workerSumMax.add(0f);
            }
        }
        averageLoad = actualLoad/workers.size();
        for (Float sumMax : workerSumMax) {
            maxWorkload += Math.pow(sumMax - averageLoad, 2);
        }
        return maxWorkload;
    }

    public List<PersonEntity> AllProjectWorkers(List<Object> projects) {
        List<PersonEntity> projectWorkers = new ArrayList<>(0);
        for (Object project : projects) {
            ProjectRole projectRole = (ProjectRole) project;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            int i = 0;
            while (i < roleWorkers.size()) {
                RoleWorker roleWorker = roleWorkers.get(i);
                List<PersonEntity> workers = new ArrayList<>(0);
                workers.addAll(roleWorker.getWorkers());
                workers.addAll(roleWorker.getFixedWorkers());

                int j = 0;
                while (j < workers.size()) {
                    PersonEntity worker = workers.get(j);

                    int k = 0;
                    boolean found = false;
                    while (k < projectWorkers.size() && !found) {
                        if (projectWorkers.get(k).getId().equals(worker.getId())) {
                            found = true;
                        }
                        k++;
                    }
                    if (!found) {
                        projectWorkers.add(worker);
                    }
                    j++;
                }
                i++;
            }
        }
        return projectWorkers;
    }
}
