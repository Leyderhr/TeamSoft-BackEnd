package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import lombok.Data;

import problem.definition.Problem;

import java.util.List;

@Data
public class TeamFormationProblem extends Problem {

    private List<ProjectRole> projects;
    private TeamFormationParameters parameters;

    public TeamFormationProblem() {
        super();
    }

    public TeamFormationProblem(List<ProjectRole> projects, TeamFormationParameters parameters) {
        this.projects = projects;
        this.parameters = parameters;
    }
}
