package com.tesis.teamsoft.metaheuristics.auxiliary.test;

import com.tesis.teamsoft.metaheuristics.operator.TeamBuilder;
import com.tesis.teamsoft.persistence.entity.CycleEntity;
import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompetenceProjectRole {

    private ProjectEntity project;
    private List<CompetenceRoleWorker> roleWorkers;

    /**
     * Retorna el ultimo ciclo del proyecto (retorna el que no tiene fecha de
     * fin)
     */
    public CycleEntity lastProjectCycle() {
        return TeamBuilder.lastProjectCycle(project);
    }
}
