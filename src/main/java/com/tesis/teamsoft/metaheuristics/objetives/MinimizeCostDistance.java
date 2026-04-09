package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.CostDistanceEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.List;

@Setter
@Getter
public class MinimizeCostDistance extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "CostoDistancia";
    
     public MinimizeCostDistance(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
    }
    
     public MinimizeCostDistance() {
        super();
    }
    
    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();
        double total_distanceCost = 0f;
        float project_distanceIndex;
        double costDistance;
        double roleImpact;
        double costDistanceIndex;
        float maxCostDistance = maxCostDistance(projects);
        float bigger = parameters.getMaxCostDistance().getCostDistance();
        float maxProjectCostDistance;
        
        for (Object item : projects) { // Para cada ProjectRole
            project_distanceIndex = 0;
            ProjectRole projectRole = (ProjectRole) item;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            maxProjectCostDistance = 0;

            for (RoleWorker rwItem : roleWorkers) {//Por cada RoleWorker del ProjectRole
                RoleEntity role = rwItem.getRole();
                roleImpact = role.getImpact();

                for (int j = 0; j < rwItem.getWorkers().size(); j++) { //Para cada trabajador de la lista de trabajadores del RoleWorker
                    PersonEntity worker = rwItem.getWorkers().get(j);
                    List<CostDistanceEntity> worker_CountyCostDistance = worker.getCounty().getCostDistanceListB();
                    costDistance = 0;
                    Long project_CountyId = projectRole.getProject().getProvince().getId();

                    int k = 0;
                    boolean found = false;
                    while (k < worker_CountyCostDistance.size() && !found) {

                        if (worker_CountyCostDistance.get(k).getCountyB().getId().equals(project_CountyId)
                                || worker_CountyCostDistance.get(k).getCountyA().getId().equals(project_CountyId)) {
                            costDistance = worker_CountyCostDistance.get(k).getCostDistance();
                            found = true;
                        }
                        k++;
                    }
                    costDistanceIndex = costDistance * roleImpact;
                    maxProjectCostDistance += (float) (bigger * roleImpact);
                    project_distanceIndex += (float) costDistanceIndex;
                }

                for (int j = 0; j < rwItem.getFixedWorkers().size(); j++) { //Para cada trabajador de la lista de trabajadores del RoleWorker
                    PersonEntity worker = rwItem.getFixedWorkers().get(j);
                    List<CostDistanceEntity> worker_CountyCostDistance = worker.getCounty().getCostDistanceListB();
                    costDistance = 0;
                    Long project_CountyId = projectRole.getProject().getProvince().getId();

                    int k = 0;
                    boolean found = false;
                    while (k < worker_CountyCostDistance.size() && !found) {

                        if (worker_CountyCostDistance.get(k).getCountyB().getId().equals(project_CountyId)
                                || worker_CountyCostDistance.get(k).getCountyA().getId().equals(project_CountyId)) {
                            costDistance = worker_CountyCostDistance.get(k).getCostDistance();
                            found = true;
                        }
                        k++;
                    }
                    costDistanceIndex = costDistance * roleImpact;
                    maxProjectCostDistance += (float) (bigger * roleImpact);
                    project_distanceIndex += (float) costDistanceIndex;
                }
            }
            total_distanceCost += project_distanceIndex;
            projectRole.getProjectEvaluation()[3] = (double) (project_distanceIndex / maxProjectCostDistance);
        }
        total_distanceCost = total_distanceCost / maxCostDistance; // min = 0
        return total_distanceCost;
    }

    public float maxCostDistance(List<Object> projects) {
        float maxCostDistance = 0;
        float roleImpact;
        float bigger = parameters.getMaxCostDistance().getCostDistance();
        
        for (Object item : projects) { // Para cada ProjectRole
            ProjectRole projectRole = (ProjectRole) item;
                       
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            for (RoleWorker rwItem : roleWorkers) {//Por cada RoleWorker del ProjectRole
                RoleEntity role = rwItem.getRole();
                roleImpact = role.getImpact();
                for (PersonEntity ignored : rwItem.getWorkers()) {
                    //Para cada trabajador de la lista de trabajadores del RoleWorker
                    maxCostDistance += bigger * roleImpact;
                }
                for (PersonEntity ignored : rwItem.getFixedWorkers()) {
                    //Para cada trabajador de la lista de trabajadores del RoleWorker
                    maxCostDistance += bigger * roleImpact;
                }
            }
        }
        return maxCostDistance;
    }
}
