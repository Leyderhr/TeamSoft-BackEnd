package com.tesis.teamsoft.metaheuristics.objetives;

import java.util.ArrayList;
import java.util.List;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.Setter;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;

/**
 *
 * @author Roidel Montenegro
 */
@Setter
@Getter
public class MinimizeSexFactor extends ObjetiveFunction {
    private TeamFormationParameters parameters;

    public static String className = "Sexo Homogéneo";

    public MinimizeSexFactor(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    public MinimizeSexFactor() {
        super();
        setTypeProblem(Problem.ProblemType.Minimizar);
    }

    @Override
    // FO= cantPersonasEquipo-|(cantMasculino-cantFemenino)|/ cantPersonasEquipo- min(Par=0 Impar=1)
   public Double Evaluation(State state) {
    double normalizarEval;
    double cantWorkerProject = 0;
    double cantMale = 0;
    double cantFemale = 0;
    double differenceSex = 0;
    double minValue;

    List<Object> projects = state.getCode();  // lista de proyectos - roles
    List<PersonEntity> workers = new ArrayList<>();

    for (Object proyecto : projects) {
        ProjectRole projectRole = (ProjectRole) proyecto; // obtener proyecto actual
        workers.addAll(ObjetiveFunctionUtil.ProjectWorkers(projectRole)); // obtener lista de trabajadores

        cantWorkerProject += workers.size(); // Obtener todos los trabajadores totales

        for (PersonEntity worker : workers) {
            if (worker.getSex() == 'M') {
                cantMale++;
            } else {
                cantFemale++;
            }
        }

        differenceSex += Math.abs(cantMale - cantFemale); // calcular diferencia modular

        workers.clear();
        cantMale = 0;
        cantFemale = 0;
    }

    if (cantWorkerProject % 2 == 0) {
        minValue = 0;
    } else {
        minValue = 1;
    }

    normalizarEval = ((cantWorkerProject - differenceSex) / (cantWorkerProject - minValue));

    return normalizarEval;
}

}