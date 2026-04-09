package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.ProjectRolesEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * @author G1lb3rt
 */
@Setter
@Getter
public class BalanceMinimizeWorkload extends ObjetiveFunction {

    public static String className = "BalanceCargaTrabajo";

    private TeamFormationParameters parameters;

    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();
        List<Double> indexList = new ArrayList<>();
        double totalLoad;
        float actualLoad;
        float newLoad;
        double maxWorkload;
        double balancedLoad;
        int total_projects = projects.size();
        double maxLoad = parameters.getMaxRoleLoad().getValue();
        List<Double> projectSumMax = new ArrayList<>();

        int amount_projects_max;
        if(total_projects%2 == 0){ //Si la cantidad de proyectos es par
            amount_projects_max = projects.size()/2;
        }
        else{
            amount_projects_max = (projects.size()+1)/2;
        }
        
        for (int k = 0; k < total_projects; k++) {
            actualLoad = 0;
            newLoad = 0;
            ProjectRole projectRole = (ProjectRole) projects.get(k);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);
            for (PersonEntity projectWorker : projectWorkers) {
                actualLoad += projectWorker.getWorkload();
            }

            for (RoleWorker rwItem : roleWorkers) {
                RoleEntity role = rwItem.getRole();

                if (!rwItem.getWorkers().isEmpty()) {
                    for (int l = 0; l < rwItem.getWorkers().size(); l++) {
                        newLoad += getRoleLoad(role, projectRole);
                    }
                }

                if (!rwItem.getFixedWorkers().isEmpty()) {
                    for (int l = 0; l < rwItem.getFixedWorkers().size(); l++) {
                        newLoad += getRoleLoad(role, projectRole);
                    }
                }
            }
            totalLoad = actualLoad + newLoad;
            indexList.add(totalLoad/(maxLoad*projectWorkers.size()));
            
             if(amount_projects_max > 0){
                    projectSumMax.add(1d); //La razón de máximo interes es 1, indicando que todos tienen preferencia por los roles asignados
                    amount_projects_max--;
                }
                else{
                    projectSumMax.add(0d); //La razón de mínimo interés es 0, indicando que nadie tiene preferencia por los roles asignados
                }  
        }
        balancedLoad = ObjetiveFunctionUtil.balance(indexList);
        
        //Calcular el máximo valor de balance
        maxWorkload = ObjetiveFunctionUtil.balance(projectSumMax);
        balancedLoad = balancedLoad / maxWorkload;
        return balancedLoad;
    }

    public float getRoleLoad(RoleEntity role, ProjectRole pr) {
        int cycleSize = pr.getProject().getCycleList().size();
        List<ProjectRolesEntity> pRolesList = pr.getProject().getCycleList().get(cycleSize - 1).getProjectStructure().getProjectRolesList();
        float roleLoad = -1;

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
}
