package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonTestEntity;
import com.tesis.teamsoft.metaheuristics.util.ProjectRole;
import com.tesis.teamsoft.metaheuristics.util.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author G1lb3rt
 */
public class MaximizeMbtiTypes extends ObjetiveFunction {

    public static String className = "Tipos MBTI";

    @Override
    public Double Evaluation(State state) {

        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles
        double sum = 0;
        double mbtiProject;

        int i = 0;
        int totalPersons = 0;
        while (i < projects.size()) { //para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
                        List<PersonEntity> verifiedWorkers = new LinkedList<>();//lista para verificar que no se repita ningun trabajador entre roles del mismo proyecto

            List<PersonEntity> team = new ArrayList<>(); //listado de personas del projecto

            int j = 0;
            while (j < roleWorkers.size()) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);
                roleWorker.getWorkers().addAll(roleWorker.getFixedWorkers());  // concatenar listas de personas y personas fijadas por el usuario
                team.addAll(roleWorker.getWorkers()); //añadir personas que juegan el rol actual a la lista de personas del proyecto

                j++;
            }

            int ISTJ = 0;
            int ISFJ = 0;
            int INTJ = 0;
            int INFJ = 0;
            int ISTP = 0;
            int ISFP = 0;
            int INTP = 0;
            int INFP = 0;
            int ESTJ = 0;
            int ESFJ = 0;
            int ENTJ = 0;
            int ENFJ = 0;
            int ESTP = 0;
            int ESFP = 0;
            int ENTP = 0;
            int ENFP = 0;

            boolean found;
            int k = 0;
            while (k < team.size()) {  //para cada persona del equipo de proyecto actual
                found = false;
                PersonEntity worker = team.get(k);
                for (PersonEntity verifiedWorker : verifiedWorkers) {
                    if (worker == verifiedWorker) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    verifiedWorkers.add(worker);
                    PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas

                    if (workerTest != null) {
                        if (workerTest.getMbtiType().equalsIgnoreCase("ISTJ") && ISTJ == 0) {
                            ISTJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ISFJ") && ISFJ == 0) {
                            ISFJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("INTJ") && INTJ == 0) {
                            INTJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("INFJ") && INFJ == 0) {
                            INFJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ISTP") && ISTP == 0) {
                            ISTP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ISFP") && ISFP == 0) {
                            ISFP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("INTP") && INTP == 0) {
                            INTP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("INFP") && INFP == 0) {
                            INFP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ESTJ") && ESTJ == 0) {
                            ESTJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ESFJ") && ESFJ == 0) {
                            ESFJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ENTJ") && ENTJ == 0) {
                            ENTJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ENFJ") && ENFJ == 0) {
                            ENFJ = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ESTP") && ESTP == 0) {
                            ESTP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ESFP") && ESFP == 0) {
                            ESFP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ENTP") && ENTP == 0) {
                            ENTP = 1;
                        }
                        if (workerTest.getMbtiType().equalsIgnoreCase("ENFP") && ENFP == 0) {
                            ENFP = 1;
                        }
                    }
                }
                k++;
            }
            mbtiProject = ISTJ + ISFJ + INTJ + INFJ + ISTP + ISFP + INTP + INFP + ESTJ + ESFJ + ENTJ + ENFJ + ESTP + ESFP + ENTP + ENFP;
            System.out.println("MBTI PROJECT: " + mbtiProject);
            sum += mbtiProject;
            totalPersons += verifiedWorkers.size();
            if (!projectRole.getProject().getProjectName().contains("MBTI_Types")) {
                projectRole.getProject().setProjectName(projectRole.getProject().getProjectName() + " MBTI_Types: " + mbtiProject / verifiedWorkers.size());
            }
            System.out.println("MBTI_Types:" + mbtiProject / verifiedWorkers.size());

            i++;
        }
        System.out.println("Total Propuesta = " + sum + "\n"); //Eliminar
        System.out.println("Maximo Posible = " + totalPersons + "\n"); //Eliminar
        sum = ((totalPersons - sum) / (totalPersons - 1));
        System.out.println("Indice Propuesta = " + sum + "\n"); //Eliminar
        return sum;
    }

}
