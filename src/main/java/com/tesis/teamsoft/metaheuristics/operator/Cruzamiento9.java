package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import com.tesis.teamsoft.persistence.entity.RoleExperienceEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cruzamiento9 extends TeamFormationOperator {

    public static ArrayList<ArrayList<Object>> OperadorCruzamiento9(ArrayList<Object> code1, ArrayList<Object> code2, Random generator) throws IOException, ClassNotFoundException {
        List<RoleWorker> roleWorkersA = tomarProyectoAleatorio(code1, generator).getRoleWorkers();
        RoleWorker roleWorkerA;
        PersonEntity workerA;
        float xp1 = 0;
        do {
            roleWorkersA = tomarProyectoAleatorio(code1, generator).getRoleWorkers();
            roleWorkerA = tomarRolAlatorio(roleWorkersA, generator);
            workerA = tomarPersonaAlatoria(roleWorkerA, generator);
            if (workerA.getRoleExperience(roleWorkerA.getRole().getId()) != null) {
                RoleExperienceEntity roleXP = workerA.getRoleExperience(roleWorkerA.getRole().getId());
                xp1 = roleXP.getIndexes();
            }
        } while (xp1 > 0.5);
        ArrayList<PersonEntity> perSol2 = personasSolucion(code2);

        float mayor = 0;
        int pos = 0;
        for (int i = 0; i < perSol2.size(); i++) {
            float xp2 = 0;

            if (perSol2.get(i).getRoleExperience(roleWorkerA.getRole().getId()) != null) {
                RoleExperienceEntity roleXP2 = perSol2.get(i).getRoleExperience(roleWorkerA.getRole().getId());
                xp2 = roleXP2.getIndexes();
            }
            RoleExperienceEntity roleXP2 = perSol2.get(i).getRoleExperience(roleWorkerA.getRole().getId());

            if (xp2 > 0.5) {

                RoleEntity rol = rolAsignadoAPersona(code1, perSol2.get(i));
                RoleExperienceEntity rExp = workerA.getRoleExperience(rol.getId());
                RoleExperienceEntity rExp2 = perSol2.get(i).getRoleExperience(rol.getId());

                if (mayor < rExp.getIndexes() - rExp2.getIndexes()) {
                    mayor = rExp.getIndexes() - rExp2.getIndexes();
                    pos = i;
                }

            }
        }
        PersonEntity workerB = perSol2.get(pos); // persona a poner en la solución hija 1

        ArrayList<ArrayList<Object>> codes = new ArrayList<>();

        ProjectRole rp, rp2;
        RoleWorker rw, rw2;
        for (int i = 0; i < code1.size(); i++) {
            rp = ((ProjectRole) code1.get(i));
            rp2 = ((ProjectRole) code2.get(i));
            for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                rw = rp.getRoleWorkers().get(j);
                rw2 = rp2.getRoleWorkers().get(j);
                for (int k = 0; k < rw.getWorkers().size(); k++) {
                    if (rw.getWorkers().get(k).getCard().equals(workerA.getCard())) {
                        rw.getWorkers().set(k, workerB);
                    }
                    if (rw2.getWorkers().get(k).getCard().equals(workerB.getCard())) {
                        rw2.getWorkers().set(k, workerA);
                    }
                }
            }

        }
        codes.add(code1);
        codes.add(code2);

        return codes;

    }

}
