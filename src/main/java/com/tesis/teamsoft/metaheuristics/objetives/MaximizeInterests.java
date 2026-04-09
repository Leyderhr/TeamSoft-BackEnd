package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonalInterestsEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.List;

/**
 * @author G1lb3rt
 */
public class MaximizeInterests extends ObjetiveFunction {

    public static String className = "Intereses";

    @Override
    public Double Evaluation(State state) {
        double total_interest = 0;
        double projectInterest;
        int maxPerson = 0;
        int projectPerson;

        List<Object> projects = state.getCode();       

        for (Object item : projects) { // Para cada ProjectRole
            ProjectRole projectRole = (ProjectRole) item;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            projectInterest = 0;
            projectPerson = 0;
            
            for (RoleWorker rwItem : roleWorkers) { //Por cada RoleWorker del ProjectRole

                projectPerson += rwItem.getWorkers().size()+rwItem.getFixedWorkers().size();
                maxPerson += rwItem.getWorkers().size()+rwItem.getFixedWorkers().size();
                List<PersonalInterestsEntity> interests;
                int i;
                boolean find;
                    
                for (PersonEntity worker : rwItem.getWorkers()) { //Para cada trabajador de la lista de trabajadores del RoleWorker
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
                }
                for (PersonEntity worker : rwItem.getFixedWorkers()) { //Para cada trabajador de la lista de trabajadores fijados del RoleWorker
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
                }
            }
            total_interest += projectInterest;
            projectRole.getProjectEvaluation()[8] = projectInterest/projectPerson;
        }
        total_interest = total_interest / maxPerson;
        return total_interest;
    }

}
