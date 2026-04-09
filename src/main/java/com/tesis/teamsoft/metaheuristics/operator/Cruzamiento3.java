package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;

import java.util.ArrayList;

/**
 * @author Zucelys
 */
public class Cruzamiento3 extends TeamFormationOperator {

    public static ArrayList<PersonEntity> OperadorCruzamiento3(ArrayList<RoleWorker> proyRoles1, ArrayList<RoleWorker> proyRoles2, int j, int k, int numVec) {

        ArrayList<PersonEntity> PersonasTemporales = new ArrayList<>();
        PersonEntity temPersona, temPersona2;
        if (numVec == 0) {
            temPersona = proyRoles1.get(j).getWorkers().get(k);
            temPersona2 = proyRoles2.get(j).getWorkers().get(k);
        } else {
            temPersona2 = proyRoles1.get(j).getWorkers().get(k);
            temPersona = proyRoles2.get(j).getWorkers().get(k);
        }
        PersonasTemporales.add(temPersona);
        PersonasTemporales.add(temPersona2);

        return PersonasTemporales;
    }

}
