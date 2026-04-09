package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import lombok.Getter;
import lombok.Setter;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;

import static com.tesis.teamsoft.metaheuristics.auxiliary.FactorEvaluation.religionEvaluation;

@Setter
@Getter
public class MinimizeReligionFactor extends ObjetiveFunction {
    private TeamFormationParameters parameters;
    public static String className = "Equipo Homogéneo Religión";

    public MinimizeReligionFactor(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    public MinimizeReligionFactor() {
        super();
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    @Override
    public Double Evaluation(State state) {
        return religionEvaluation(state.getCode(), parameters.getSearchArea());
    }
}