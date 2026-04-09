package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Zucelys
 */
public class Cruzamiento2 extends TeamFormationOperator {


    public static ArrayList<ProjectRole> OperadorCruzamiento2(ProjectRole proyCode1, ProjectRole proyCode2) {
        ProjectRole proy = new ProjectRole(proyCode1.getProject(), proyCode1.getProjectEvaluation(), new ArrayList<>());
        ProjectRole proy2 = new ProjectRole(proyCode2.getProject(), proyCode2.getProjectEvaluation(), new ArrayList<>());
        ArrayList<ProjectRole> proyects = new ArrayList<>();
        Random random = new Random();
        ArrayList<Integer> vector = new ArrayList<>();

        for (int i = 0; i < proyCode1.getRoleWorkers().size(); i++) {
            vector.add(random.nextInt(2));
        }

        for (int i = 0; i < proyCode1.getRoleWorkers().size(); i++) {
            if (vector.get(i) == 0) {
                proy.getRoleWorkers().add(proyCode1.getRoleWorkers().get(i));
                proy2.getRoleWorkers().add(proyCode2.getRoleWorkers().get(i));
            } else {
                proy.getRoleWorkers().add(proyCode2.getRoleWorkers().get(i));
                proy2.getRoleWorkers().add(proyCode1.getRoleWorkers().get(i));
            }
        }
        proyects.add(proy);
        proyects.add(proy2);
        return proyects;
    }


}
