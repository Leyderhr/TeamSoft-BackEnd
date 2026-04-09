package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonalInterestsEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author G1lb3rt
 */
public class BalanceMaximizeInterests extends ObjetiveFunction {

    public static String className = "BalanceIntereses";
    
    @Override
    public Double Evaluation(State state) {
        double total_interest;
        double total_interest_max;
        double projectInterest;
        List<Double> projectSum = new ArrayList<>();
        List<Double> projectSumMax = new ArrayList<>();

        int projectPerson;
        List<PersonalInterestsEntity> interests;
        int i;
        boolean find;
        List<Object> projects = state.getCode();
        
        int amount_projects_max;
        if(projects.size()%2 == 0){ //Si la cantidad de proyectos es par
            amount_projects_max = projects.size()/2;
        }
        else{
            amount_projects_max = (projects.size()+1)/2;
        }
                
        for (Object item : projects) { // Para cada Proyecto -Rol 
            ProjectRole projectRole = (ProjectRole) item;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            projectInterest = 0;
            projectPerson = 0;
            
            for (RoleWorker rwItem : roleWorkers) { //Por cada RoleWorker del ProjectRole

                projectPerson += rwItem.getWorkers().size() + rwItem.getFixedWorkers().size();
                for (PersonEntity worker : rwItem.getWorkers()) { //Para cada trabajador de la lista de trabajadores del RoleWorker
                    projectInterest = getProjectInterest(projectInterest, rwItem, worker);
                }
                for (PersonEntity worker : rwItem.getFixedWorkers()) { //Para cada trabajador de la lista de trabajadores fijados del RoleWorker
                    projectInterest = getProjectInterest(projectInterest, rwItem, worker);
                }
            }
            projectSum.add(projectInterest/projectPerson);
            
            if(amount_projects_max > 0){
                    projectSumMax.add(1d); //La razón de máximo interes es 1, indicando que todos tienen preferencia por los roles asignados
                    amount_projects_max--;
                }
                else{
                    projectSumMax.add(0d); //La razón de mínimo interés es 0, indicando que nadie tiene preferencia por los roles asignados
                }            
        }
        total_interest = ObjetiveFunctionUtil.balance(projectSum); //balancear funcion objetivo
        
        //Calcular el máximo valor de balance
        total_interest_max = ObjetiveFunctionUtil.balance(projectSumMax);
        
        total_interest = total_interest / total_interest_max; // normalizar resultado (valor entre 0-1)

        return total_interest;
    }

    private double getProjectInterest(double projectInterest, RoleWorker rwItem, PersonEntity worker) {
        List<PersonalInterestsEntity> interests;
        int i;
        boolean find;
        interests = worker.getPersonalInterestsList();
        i = 0;
        find = false;
        while (i < interests.size() && !find) { //Recorrer lista de intereces del trabajador y verificar preferencia por rol
            if (interests.get(i).getRole().getRoleName().equalsIgnoreCase(rwItem.getRole().getRoleName())) { //si es el rol buscado
                if (interests.get(i).isPreference()) { // si lo prefiere
                    projectInterest += 1;
                }
                find = true;
            }
            i++;
        }
        if (!find) {
            projectInterest += 0.5;
        }
        return projectInterest;
    }
}
