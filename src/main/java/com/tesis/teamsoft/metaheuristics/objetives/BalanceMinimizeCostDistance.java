package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.CostDistanceEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMinimizeCostDistance extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "BalanceCostoDistancia";


    @Override
    public Double Evaluation(State state) {
        List<Object> projects = state.getCode();
        List<Double> indexList = new ArrayList<>();
        double project_distanceIndex;
        double project_distanceMax;
        double costDistance;
        double roleImpact;
        double costDistanceIndex;
        double maxCostDistance = parameters.getMaxCostDistance().getCostDistance();
        double balance;
        List<Double> projectSumMax = new ArrayList<>();

        int amount_projects_max;
        if (projects.size() % 2 == 0) { //Si la cantidad de proyectos es par
            amount_projects_max = projects.size() / 2;
        } else {
            amount_projects_max = (projects.size() + 1) / 2;
        }

        for (Object item : projects) {//Para cada ProjectRole
            project_distanceIndex = 0;
            project_distanceMax = 0;
            ProjectRole projectRole = (ProjectRole) item;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

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
                    project_distanceIndex += costDistanceIndex;
                    project_distanceMax += maxCostDistance * roleImpact;
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
                    project_distanceIndex += costDistanceIndex;
                    project_distanceMax += maxCostDistance * roleImpact;
                }
            }
            indexList.add(project_distanceIndex / project_distanceMax);

            if (amount_projects_max > 0) {
                projectSumMax.add(1d); //La razón de máximo costo es 1
                amount_projects_max--;
            } else {
                projectSumMax.add(0d); //La razón de mínimo costo es 0
            }
        }
        balance = ObjetiveFunctionUtil.balance(indexList);

        //Calcular el máximo valor de balance
        maxCostDistance = ObjetiveFunctionUtil.balance(projectSumMax);

        balance = balance / maxCostDistance; // min = 0
        return balance;
    }
}
