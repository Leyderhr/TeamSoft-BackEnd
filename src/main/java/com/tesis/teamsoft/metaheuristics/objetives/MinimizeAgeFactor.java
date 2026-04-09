package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import lombok.Getter;
import lombok.Setter;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;

import static com.tesis.teamsoft.metaheuristics.auxiliary.FactorEvaluation.ageGroupEvaluation;

@Setter
@Getter
public class MinimizeAgeFactor extends ObjetiveFunction {

    private TeamFormationParameters parameters;
    public static String className = "Equipo Homogéneo Edad";

    public MinimizeAgeFactor(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    public MinimizeAgeFactor() {
        super();
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    @Override
    public Double Evaluation(State state) {
        return ageGroupEvaluation(state.getCode(), parameters.getSearchArea());
    }
}

