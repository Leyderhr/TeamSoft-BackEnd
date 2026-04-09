package com.tesis.teamsoft.metaheuristics.objetives;

import java.util.ArrayList;
import java.util.List;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.NacionalityEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;

/**
 *
 * @author Yenissey
 */
@Setter
@Getter
public class MinimizeNacionalityFactor extends ObjetiveFunction{
    private TeamFormationParameters parameters;

    public static String className = "Equipo Homogéneo";

    public MinimizeNacionalityFactor(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    public MinimizeNacionalityFactor() {
        super();
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    @Override
    public Double Evaluation(State state) {

        double minValueNacOrganizationList = 1;
        double minValueNacTeamList = 1;
        double maxValue;
        double normalizeCoef;

        List<Object> projects = state.getCode();  //lista de proyectos - roles
        List<PersonEntity> teamWorkerList = new ArrayList<>();
        List<NacionalityEntity> nacionalityList = new ArrayList<>();
        List<PersonEntity> organizationWorkerList = parameters.getSearchArea();
        List<Integer> amountTeamList= new ArrayList<>(); // cant de nac por  equipos

        int p = 0; //para iterar por los equipos
        int amountNacionalitySearchArea; // Cantidad de nacionalidades en la organiación
        double sum = 0;

        //Cantidad de nacionalidades diferentes del searchArea
        for (PersonEntity worker : organizationWorkerList) {
            if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                nacionalityList.add(worker.getNacionality());
            }
        }
        amountNacionalitySearchArea = nacionalityList.size();  //Cantidad de nacionalidades en la organización

        //Cantidad de nacionalidades diferentes por equipo
        while (p < projects.size()) { // recorrer todos los proyectos
            nacionalityList.clear();
            ProjectRole projectRole = (ProjectRole) projects.get(p); // obtener proyecto actual

            teamWorkerList = ObjetiveFunctionUtil.ProjectWorkers(projectRole);

            //iterar por todos los trabajadores del equipos
            for (PersonEntity worker : teamWorkerList) {
                if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                    nacionalityList.add(worker.getNacionality());
                }
            }
            amountTeamList.add(nacionalityList.size());
            p++;
        }

        maxValue = Math.min(teamWorkerList.size(), amountNacionalitySearchArea);

        for (Integer integer : amountTeamList) {
            sum += (integer - minValueNacTeamList) / (maxValue - minValueNacOrganizationList);
        }

        normalizeCoef= sum/amountTeamList.size();

        return normalizeCoef;
    }

}
