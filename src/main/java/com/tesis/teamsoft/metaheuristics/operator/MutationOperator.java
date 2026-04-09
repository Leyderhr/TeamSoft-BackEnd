package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.tesis.teamsoft.metaheuristics.operator.TeamFormationOperator.tomarPersonaAlatoria;
import static com.tesis.teamsoft.metaheuristics.operator.TeamFormationOperator.tomarRolAlatorio;

public class MutationOperator {

    public static void OperadorMutacion0(RoleWorker roleWorkerA, TeamFormationCodification codification, Random generator) {
        //Sustituye UNA PERSONA DE UN PROYECTO POR OTRA DE LA CODIFICACION
        int personIndexB = generator.nextInt(codification.getSearchArea().size());
        PersonEntity workerB = codification.getSearchArea().get(personIndexB);//tomar persona B de la lista
        roleWorkerA.getWorkers().set(0, workerB);//poner persona B en lugar de A
    }

    public static void OperadorMutacion1(RoleWorker roleWorkerA, List<RoleWorker> roleWorkersA, Random generator) {

        //INTERCAMBIA 2 PERSONAS DE ROLES DIFERENTES EN UN MISMO PROYECTO
        RoleWorker roleWorkerB = tomarRolAlatorio(roleWorkersA, generator);
        if (roleWorkersA.size() != 1) {
            //Validar q se escojan roles distintos si se define + de un rol en el project
            while ((roleWorkerA.getRole().getId()).equals(roleWorkerB.getRole().getId())) {
                roleWorkerB = tomarRolAlatorio(roleWorkersA, generator);
            }
        }
        PersonEntity workerA = tomarPersonaAlatoria(roleWorkerA, generator);
        PersonEntity workerB = tomarPersonaAlatoria(roleWorkerB, generator);

        roleWorkerA.getWorkers().add(workerB);//poner persona B en lugar de A
        roleWorkerB.getWorkers().add(workerA);//poner persona A en lugar de B
    }

    public static void OperadorMutacion2(RoleWorker roleWorkerA, List<RoleWorker> roleWorkersB, Random generator) {
        //INTERCAMBIA 2 PERSONAS DE PROYECTOS DIFERENTES
        PersonEntity workerA = tomarPersonaAlatoria(roleWorkerA, generator);
        RoleWorker roleWorkerB = tomarRolAlatorio(roleWorkersB, generator);

        PersonEntity workerB = tomarPersonaAlatoria(roleWorkerB, generator);
        roleWorkerA.getWorkers().add(workerB);//poner persona B en lugar de A
        roleWorkerB.getWorkers().add(workerA);//poner persona A en lugar de B
    }

    /**
     * se buscan dos roles aleatorios, si uno de esos es el líder, se realiza el cambio por
     * todos los roles que tiene asignado el lider
     * <p>
     * Ej. Si hay 3 roles Lider=Pepito; Programador=Jose,Ana; Tester=Luna, Pepito
     * y se escoje a Pepito y a Jose para realizar el swap, josé pasa a lider y a tester
     * Lider=Jose; Programador=Pepito,Ana; Tester=Luna, Jose
     */
    public static void performPermutationBoss(ArrayList<Object> projects, TeamFormationCodification codification, Random generator) {
        /*dame un proyecto aleatorio*/
        ProjectRole project = ((ProjectRole) projects.get(generator.nextInt(projects.size())));
        /*dame la lista de roleWorkers*/
        List<RoleWorker> allProjectRoles = project.getRoleWorkers();
        List<RoleWorker> copyProjectRoles = new LinkedList<>(allProjectRoles); // lista auxiliar

        RoleWorker roleBoss = project.getProjectBoss();
        if (allProjectRoles.size() != 1) { // si tiene más de un rol
            /*dame un rol aleatorio*/
            RoleWorker roleA = allProjectRoles.get(generator.nextInt(allProjectRoles.size()));
            copyProjectRoles.remove(roleA); // quito de los disponibles al rol A así garantizo que sean distintos
            RoleWorker roleB = copyProjectRoles.get(generator.nextInt(copyProjectRoles.size()));

            if (roleA.equals(roleBoss) || roleB.equals(roleBoss)) { // si el jefe fue uno de ellos
                PersonEntity workerBoss = roleBoss.getWorkers().getFirst();
                RoleWorker roleToSwap = roleBoss.equals(roleB) ? roleA : roleB; // dame el que no es boss

                /*dame una persona aleatoria del rol a cambiar*/
                PersonEntity workerToSwap = roleToSwap.getWorkers().get(generator.nextInt(roleToSwap.getWorkers().size()));

                /*si eres jefe cambialo por el swap, si eres el swap cambialo por el jefe*/
                for (RoleWorker roleWorker : allProjectRoles) {
                    for (PersonEntity worker : roleWorker.getWorkers()) {
                        if (worker.equals(workerBoss)) {
                            int index = roleWorker.getWorkers().indexOf(worker);
                            roleWorker.getWorkers().set(index, workerToSwap);
                        } else if (worker.equals(workerToSwap)) {
                            int index = roleWorker.getWorkers().indexOf(worker);
                            roleWorker.getWorkers().set(index, workerToSwap);
                        }
                    }
                }
            } else {
                PersonEntity workerA = roleA.getWorkers().get(generator.nextInt(roleA.getWorkers().size())); //tomar persona A de la lista
                PersonEntity workerB = roleB.getWorkers().get(generator.nextInt(roleB.getWorkers().size()));//tomar persona B de la lista

                /*los borro de su lista de trabajadores*/
                roleA.getWorkers().remove(workerA);
                roleB.getWorkers().remove(workerB);

                /*hago el swap*/
                roleA.getWorkers().add(workerB);
                roleB.getWorkers().add(workerA);
            }
        } else {
            throw new RuntimeException("No se puede realizar la permutación si solo tiene un rol");
        }

    }

    /**
     * realiza la sustitucion por todas las ocurrencias
     * de la persona elegida en el proyecto
     */
    public static void performExternalSubtitution(ArrayList<Object> projects, TeamFormationCodification codification, Random generator) {
        /*dame un proyecto aleatorio*/
        ProjectRole project = ((ProjectRole) projects.get(generator.nextInt(projects.size())));
        /*dame la lista de roleWorkers*/
        List<RoleWorker> allProjectRoles = project.getRoleWorkers();
        List<PersonEntity> allWorkers = codification.getSearchArea();
        if (allProjectRoles.size() != 1) { // si tiene más de un rol
            /*dame un rol aleatorio*/
            RoleWorker randomRole = allProjectRoles.get(generator.nextInt(allProjectRoles.size()));
            PersonEntity randomWorkerOfRandomRole = randomRole.getWorkers().get(generator.nextInt(randomRole.getWorkers().size()));
            PersonEntity workerToSwap = allWorkers.get(generator.nextInt(allWorkers.size()));

            for (RoleWorker roleWorker : allProjectRoles) {
                for (PersonEntity worker : roleWorker.getWorkers()) {
                    if (worker.equals(randomWorkerOfRandomRole)) { // si es igual al que quiero cambiar
                        int index = roleWorker.getWorkers().indexOf(worker);
                        roleWorker.getWorkers().set(index, workerToSwap);
                    }
                }
            }
        } else {
            throw new RuntimeException("No se puede realizar la permutación si solo tiene un rol");
        }

    }
}
