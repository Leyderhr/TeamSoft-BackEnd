package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.PersonConflictEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.List;

@Setter
@Getter
public class MinimizeIncompatibilities extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "Incompatibilidades";
    
    public MinimizeIncompatibilities(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
    }
    
    public MinimizeIncompatibilities() {
        super();
    }
    
    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();
        double total_incomp = 0;
        double maxIncompatibilities = parameters.getMaxConflictIndex().getWeight();
        double maxIncomp = 0;
        double max_project_incomp;
        for (Object item : projects) { // Para cada ProjectRole
            double project_incomp = 0;
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
                    maxIncomp += maxIncompatibilities;
                }
            }
            total_incomp += project_incomp;            
            projectRole.getProjectEvaluation()[4] = project_incomp/max_project_incomp;
        }
       total_incomp = total_incomp / maxIncomp; // Min = 0
        return total_incomp;
    }

    public Double getIncompIndex(PersonEntity worker, PersonEntity worker2) {
        double incompIndex = 0.5;
        List<PersonConflictEntity> workerConflicts = worker.getPersonConflictWithList();
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
