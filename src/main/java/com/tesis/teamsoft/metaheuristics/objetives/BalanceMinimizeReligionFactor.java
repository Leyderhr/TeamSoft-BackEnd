package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import static com.tesis.teamsoft.metaheuristics.auxiliary.FactorEvaluation.religionBalanceEvaluation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMinimizeReligionFactor extends ObjetiveFunction {
    private TeamFormationParameters parameters;
    public static String className = "Balancear equipos heterogéneos religión";

    @Override
    public Double Evaluation(State state) {
        return religionBalanceEvaluation(state.getCode(), parameters.getSearchArea());
    }
}