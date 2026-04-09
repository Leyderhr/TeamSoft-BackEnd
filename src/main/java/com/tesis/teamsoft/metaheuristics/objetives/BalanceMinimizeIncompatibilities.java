package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.PersonConflictEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMinimizeIncompatibilities extends ObjetiveFunction {

    private TeamFormationParameters parameters;
    public static String className = "BalanceIncompatibilidades";
    
    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();        
        List<Double> indexList = new ArrayList<>();
        double maxBalanceIncomp;
        double maxIncompatibilities = parameters.getMaxConflictIndex().getWeight();

        double max_project_incomp;
        List<Double> projectSumMax = new ArrayList<>();

        int amount_projects_max;
        if(projects.size()%2 == 0){ //Si la cantidad de proyectos es par
            amount_projects_max = projects.size()/2;
        }
        else{
            amount_projects_max = (projects.size()+1)/2;
        }
        
        double project_incomp;
        for (Object item : projects) { // Para cada ProjectRole
            project_incomp = 0;
            max_project_incomp = 0;
            ProjectRole projectRole = (ProjectRole) item;
            List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

            for (int j = 0; j < projectWorkers.size(); j++) {
                PersonEntity worker = projectWorkers.get(j);
                for (int i = j + 1; i < projectWorkers.size(); i++) {
                    PersonEntity projectWorker = projectWorkers.get(i);
                    Double projWorkerIndex = getIncompIndex(projectWorker, worker);
                    Double workerIndex = getIncompIndex(worker, projectWorker);
                    if (projWorkerIndex > workerIndex) {
                        project_incomp += projWorkerIndex;
                    } else {
                        project_incomp += workerIndex;
                    }
                    max_project_incomp += maxIncompatibilities;
                }
            }
            indexList.add(project_incomp/max_project_incomp);
            
             if(amount_projects_max > 0){
                    projectSumMax.add(1d); //Indica que todos lo miembros del equipo son incompatibles
                    amount_projects_max--;
                }
                else{
                    projectSumMax.add(0d); //La razón de mínima incomp es 0, indicando que son compatibles todos los miembros del equipo
                } 
            
        }
        double balance = ObjetiveFunctionUtil.balance(indexList);
        
        //Calcular el máximo valor de balance
        maxBalanceIncomp = ObjetiveFunctionUtil.balance(projectSumMax);
        
        balance = balance / maxBalanceIncomp; // Min = 0
        return balance;
    }

    public Double getIncompIndex(PersonEntity worker, PersonEntity worker2) {
        double incompIndex = 0.5;
        List<PersonConflictEntity> workerConflicts = worker.getPersonConflictList();
        int l = 0;
        boolean foundConflict = false;
        while (l < workerConflicts.size() && !foundConflict) {
            PersonConflictEntity workerConflict = workerConflicts.get(l);
            if (worker2.getId().equals(workerConflict.getPersonConflict().getId())
                    || worker2.getId().equals(workerConflict.getPerson().getId())) {
                incompIndex = workerConflict.getIndex().getWeight();
                foundConflict = true;
            }
            l++;
        }
        return incompIndex;
    }
}
