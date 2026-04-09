package com.tesis.teamsoft.metaheuristics.objetives;

import java.util.ArrayList;
import java.util.List;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.NacionalityEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

/**
 *
 * @author Yenissey
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMaximizeNacionalityFactor extends ObjetiveFunction{
    private TeamFormationParameters parameters;

    public static String className = "Balancear equipos heterogéneos formados";

    @Override
    public Double Evaluation(State state) {
        double balanceCoef;
        double sumCoef = 0;

        List<Object> projects = state.getCode();  // Lista de proyectos - roles
        List<PersonEntity> teamWorkerList;
        List<PersonEntity> organizationWorkerList = parameters.getSearchArea();
        List<NacionalityEntity> nacionalityList = new ArrayList<>();
        List<Integer> amountTeamList = new ArrayList<>(); //Cant de nac por equipo

        int sum = 0;
        int amountNacionalitySearchArea;

        // Cantidad de nacionalidades diferentes del searchArea
        for (PersonEntity worker : organizationWorkerList) {
            if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                nacionalityList.add(worker.getNacionality());
            }
        }
        amountNacionalitySearchArea = nacionalityList.size(); //Cantidad de nacionalidades en la organización

        int projectsSize = projects.size();

        for (Object proyecto : projects) {
            nacionalityList.clear();
            ProjectRole projectRole = (ProjectRole) proyecto;

            teamWorkerList = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

            for (PersonEntity worker : teamWorkerList) {
                if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                    nacionalityList.add(worker.getNacionality());
                }
            }

            amountTeamList.add(nacionalityList.size());
            sum += nacionalityList.size();
        }

        amountNacionalitySearchArea = (amountNacionalitySearchArea == 0) ? 1 : amountNacionalitySearchArea;

        for (Integer integer : amountTeamList) {
            System.out.println(": " + integer);
            sumCoef += Math.abs((sum / projectsSize) - integer);
        }

        balanceCoef = sumCoef / (projectsSize * amountNacionalitySearchArea);

        return balanceCoef;
    }

}