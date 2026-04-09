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
public class MaximizeNacionalityFactor extends ObjetiveFunction{
    private TeamFormationParameters parameters;

    public static String className = "Equipo Heterogéneo";

    public MaximizeNacionalityFactor(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(Problem.ProblemType.Maximizar);
    }

    public MaximizeNacionalityFactor() {
        super();
        setTypeProblem(Problem.ProblemType.Maximizar);
    }

    @Override
    public Double Evaluation(State state) {
        
        double minValueNacOrganizationList = 1;
        double minValueNacTeamList = 1;
        double maxValue;

        List<Object> projects = state.getCode();  //lista de proyectos - roles
        List<PersonEntity> teamWorkerList = new ArrayList<>();
        List<NacionalityEntity> nacionalityList = new ArrayList<>();
        List<PersonEntity> organizationWorkerList = parameters.getSearchArea();
        List<Integer> amountTeamList = new ArrayList<>(); //Cant de nac por equipos

        int p = 0; //para iterar por los equipos
        int amountNacionalitySearchArea; // cant Nacionalidades de la organización
        double sum = 0;
        double  normalizeCoef;
        

        nacionalityList.add(organizationWorkerList.getFirst().getNacionality());

        //Cantidad de nacionalidades diferentes del searchArea
        for (int i = 1; i < organizationWorkerList.size(); i++) {
            PersonEntity worker = organizationWorkerList.get(i);
            if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                nacionalityList.add(worker.getNacionality());
            }
        }
        amountNacionalitySearchArea = nacionalityList.size(); //Cantidad de nacionalidades en la organización

        //Cantidad de nacionalidades diferentes por equipo
        while (p < projects.size()) { // recorrer todos los proyectos
            nacionalityList.clear();
            ProjectRole projectRole = (ProjectRole) projects.get(p); // obtener proyecto actual
            
            teamWorkerList = ObjetiveFunctionUtil.ProjectWorkers(projectRole);
            
            nacionalityList.add(teamWorkerList.getFirst().getNacionality());
            
            //iterar por todos los trabajadores del equipos
            for (int i = 1; i < teamWorkerList.size(); i++) {
                PersonEntity worker = teamWorkerList.get(i);
                if (!ObjetiveFunctionUtil.foundNacionality(worker.getNacionality(), nacionalityList)) {
                    nacionalityList.add(worker.getNacionality());
                }
            }
           amountTeamList .add(nacionalityList.size());
            p++;
        }

        maxValue = Math.min(teamWorkerList.size(), amountNacionalitySearchArea);

        for (Integer integer : amountTeamList) {
            sum += (integer - minValueNacTeamList) / (maxValue - minValueNacOrganizationList);
        }      
        
       normalizeCoef = sum/amountTeamList.size(); 
        return  normalizeCoef;
    }
}

