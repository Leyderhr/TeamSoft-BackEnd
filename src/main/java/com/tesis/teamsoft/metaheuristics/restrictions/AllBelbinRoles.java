package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonTestEntity;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Que estén presentes en el equipo todos los roles de belbin, aunque esten
 * repetidos en una misma persona
 */
public class AllBelbinRoles extends Constrain {

    public AllBelbinRoles() {
    }

    @Override
    public Boolean ValidateState(State state) {

        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles

        int i = 0;
        boolean meet = true;

        while (i < projects.size() && meet) { //para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            List<PersonEntity> team = new ArrayList<>(); //listado de personas del projecto

            int j = 0;
            while (j < roleWorkers.size()) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());

                team.addAll(aux); //añadir personas que juegan el rol actual a la lista de personas del proyecto

                j++;
            }

            meet = validateProject(team);
            i++;
        }
        return meet;
    }

    public boolean validateProject(List<PersonEntity> team) {

        boolean meet = false;
        boolean CE = false;
        boolean ME = false;
        boolean ES = false;
        boolean ID = false;
        boolean IS = false;
        boolean IF = false; //este estaba como FI en la solucion anterior, confusion? o significaba otra cosa que ya no se tiene en cuenta
        boolean CO = false;
        boolean CH = false;
        boolean IR = false;
        int k = 0;
        while (k < team.size() && !meet) {  //para cada persona del equipo de proyecto actual
            PersonEntity worker = team.get(k);
            PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas

            if (workerTest != null) {
                if (workerTest.getIM() != 'I' && workerTest.getIM() != 'E') {
                    ID = true;
                }
                if (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') {
                    IS = true;
                }
                if (workerTest.getIF() != 'I' && workerTest.getIF() != 'E') {
                    IF = true;
                }
                if (workerTest.getCE() != 'I' && workerTest.getCE() != 'E') {
                    CE = true;
                }
                if (workerTest.getME() != 'I' && workerTest.getME() != 'E') {
                    ME = true;
                }
                if (workerTest.getES() != 'I' && workerTest.getES() != 'E') {
                    ES = true;
                }
                if (workerTest.getCO() != 'I' && workerTest.getCO() != 'E') {
                    CO = true;
                }
                if (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') {
                    CH = true;
                }
                if (workerTest.getIR() != 'I' && workerTest.getIR() != 'E') {
                    IR = true;
                }

                if (ID && IS && IF && CE && ME && ES && CO && CH && IR) {
                    meet = true;
                }
            }
            k++;
        }
        return meet;
    }


    public void RepareState(State state, List<PersonEntity> team, int posProy) {

        ArrayList<String> rolesQHay = rolesQueHay(team);

        ArrayList<PersonEntity> candidatos = new ArrayList<>();

        ArrayList<String> debenHaber = new ArrayList<>() {{
            add("ID");
            add("IS");
            add("IF");
            add("CE");
            add("ME");
            add("ES");
            add("CO");
            add("CH");
            add("IR");
        }};

        ArrayList<String> rolesFaltan = new ArrayList<>();
        ArrayList<String> rolesRepetidos = new ArrayList<>();

        for (String value : debenHaber) {
            int c = 0;
            for (String s : rolesQHay) {
                if (value.equals(s)) {
                    c++;
                }
            }
            if (c > 1)
                if (!(rolesRepetidos.contains(value))) {
                    rolesRepetidos.add(value);
                }
            if (c == 0) {
                rolesFaltan.add(value);
            }
        }

        if (!rolesFaltan.isEmpty()) {
            candidatos = candidatos(rolesFaltan);

        }
        //tienen que haber candidatos suficientes para reparar la solucion y roles repetidos suficientes para sustituir
        if (candidatos.size() >= rolesFaltan.size() && rolesRepetidos.size() >= rolesFaltan.size()) {
            repareProjectSolution(state, posProy, rolesRepetidos, candidatos);
        }
    }

    public ArrayList<String> rolesQueHay(List<PersonEntity> team) {
        ArrayList<String> rolesQHay = new ArrayList<>();

        int k = 0;
        while (k < team.size()) {  //para cada persona del equipo de proyecto actual
            PersonEntity worker = team.get(k);
            PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas

            if (workerTest != null) {
                if (workerTest.getIM() != 'I' && workerTest.getIM() != 'E') {
                    rolesQHay.add("ID");
                }
                if (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') {
                    rolesQHay.add("IS");

                }
                if (workerTest.getIF() != 'I' && workerTest.getIF() != 'E') {
                    rolesQHay.add("IF");

                }
                if (workerTest.getCE() != 'I' && workerTest.getCE() != 'E') {
                    rolesQHay.add("CE");

                }
                if (workerTest.getME() != 'I' && workerTest.getME() != 'E') {
                    rolesQHay.add("ME");

                }
                if (workerTest.getES() != 'I' && workerTest.getES() != 'E') {
                    rolesQHay.add("ES");

                }
                if (workerTest.getCO() != 'I' && workerTest.getCO() != 'E') {
                    rolesQHay.add("CO");

                }
                if (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') {
                    rolesQHay.add("CH");

                }
                if (workerTest.getIR() != 'I' && workerTest.getIR() != 'E') {
                    rolesQHay.add("IR");

                }
            }
            k++;
        }
        return rolesQHay;
    }

    public ArrayList<PersonEntity> candidatos(ArrayList<String> rolesFaltan) {
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        ArrayList<PersonEntity> candidatos = new ArrayList<>();
        int i = 0;
        while (i < codification.getSearchArea().size() && (candidatos.size() < rolesFaltan.size())) {
            PersonEntity worker = codification.getSearchArea().get(i);
            PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas
            boolean stop = false;
            for (int j = 0; j < rolesFaltan.size() && !stop; j++) {

                switch (rolesFaltan.get(j)) {
                    case "ID":
                        if (workerTest.getIM() != 'I' && workerTest.getIM() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "IS":
                        if (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "IF":
                        if (workerTest.getIF() != 'I' && workerTest.getIF() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "CE":
                        if (workerTest.getCE() != 'I' && workerTest.getCE() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "ME":
                        if (workerTest.getME() != 'I' && workerTest.getME() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "ES":
                        if (workerTest.getES() != 'I' && workerTest.getES() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "CO":
                        if (workerTest.getCO() != 'I' && workerTest.getCO() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "CH":
                        if (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                    case "IR":
                        if (workerTest.getIR() != 'I' && workerTest.getIR() != 'E') {
                            candidatos.add(worker);
                            stop = true;
                        }
                        break;
                }

            }

            i++;
        }
        return candidatos;
    }


    public void repareProjectSolution(State state, int posProy, ArrayList<String> rolesRepetidos, ArrayList<PersonEntity> candidatos) {
        ArrayList<Object> code = state.getCode();
        ProjectRole projectRole = (ProjectRole) code.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        int j = 0;
        while (j < roleWorkers.size() && !candidatos.isEmpty()) {
            RoleWorker rw = roleWorkers.get(j);
            int cambio = 0;
            for (int k = 0; k < rw.getWorkers().size(); k++) {
                PersonEntity worker = rw.getWorkers().get(k);
                PersonTestEntity workerTest = worker.getPersonTest(); //obtener caracteristicas psicologicas
                int a = 0;
                while (a < rolesRepetidos.size() && !candidatos.isEmpty() && cambio < 1) {

                    switch (rolesRepetidos.get(a)) {
                        case "ID":
                            if (workerTest.getIM() != 'I' && workerTest.getIM() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();
                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);

                                cambio++;

                            }
                            break;
                        case "IS":
                            if (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();
                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "IF":
                            if (workerTest.getIF() != 'I' && workerTest.getIF() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "CE":
                            if (workerTest.getCE() != 'I' && workerTest.getCE() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "ME":
                            if (workerTest.getME() != 'I' && workerTest.getME() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "ES":
                            if (workerTest.getES() != 'I' && workerTest.getES() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "CO":
                            if (workerTest.getCO() != 'I' && workerTest.getCO() != 'E') {
                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, candidatos.removeFirst());
                                cambio++;

                            }
                            break;

                        case "CH":
                            if (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;

                            }
                            break;

                        case "IR":
                            if (workerTest.getIR() != 'I' && workerTest.getIR() != 'E') {
                                PersonEntity wor = candidatos.removeFirst();

                                ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, wor);
                                cambio++;
                            }
                            break;
                    }
                    a++;
                }
            }
            j++;
        }
    }
}


