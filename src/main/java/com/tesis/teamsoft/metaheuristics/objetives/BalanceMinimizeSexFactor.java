package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Roidel Montenegro
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMinimizeSexFactor extends ObjetiveFunction {
    
    public static String className = "Balancear equipos Homogéneos";

    private TeamFormationParameters parameters;

    @Override
    public Double Evaluation(State state) {
    double balance;
    double valor = 0;
    double difSexProject;
    double cantProject = 0;
    double cantWorkersMaxProject = 0;
    double cantMale = 0;
    double cantFemale = 0;
    double amount = 0;
    double media;

    List<Double> sex = new ArrayList<>();
    List<Object> projects = state.getCode();  // lista de proyectos
    List<PersonEntity> workers = new ArrayList<>();

    for (Object proyecto : projects) {
        cantProject++;

        ProjectRole projectRole = (ProjectRole) proyecto; // obtener proyecto actual
        workers.addAll(ObjetiveFunctionUtil.ProjectWorkers(projectRole)); // obtener lista de trabajadores

        if (cantWorkersMaxProject < workers.size()) {
            cantWorkersMaxProject = workers.size();
        }

        for (PersonEntity worker : workers) {
            if (worker.getSex() == 'M') {
                cantMale++;
            } else {
                cantFemale++;
            }
        }

        difSexProject = Math.abs(cantMale - cantFemale); // obtener diferencia modular del sexo por equipo
        sex.add(difSexProject);
        amount += difSexProject;

        workers.clear();
        cantMale = 0;
        cantFemale = 0;
    }

    media = amount / cantProject; // calcular media

    for (Double difSex : sex) { // calcular sumatoria de las diferencias menos la media
        valor += difSex - media;
    }

    balance = (valor / (cantProject * cantWorkersMaxProject));

    return balance;
}
       
}