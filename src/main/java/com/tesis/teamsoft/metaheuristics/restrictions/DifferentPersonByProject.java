package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.metaheuristics.operator.TeamFormationOperator;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tesis.teamsoft.metaheuristics.restrictions.WorkerNotRepeatedInSameRole.getWorkerOcurrences;

/**
 * Una persona solo puede estar en un proyecto
 *
 * @author G1lb3rt
 */
public class DifferentPersonByProject extends Constrain {

    public static ArrayList<PersonEntity> personasSolucionNoProyect(List<Object> projects, int PosProy) {
        // devuelve un arreglo con las personas que hay en la solución pero que no pertenecen a un proyecto determinado
        ProjectRole rp;
        RoleWorker rw;

        ArrayList<PersonEntity> personas = new ArrayList<>();
        for (int i = 0; i < projects.size(); i++) {
            if (i != PosProy) {
                rp = ((ProjectRole) projects.get(i));
                for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                    rw = rp.getRoleWorkers().get(j);
                    personas.addAll(rw.getWorkers());
                }
            }
        }
        return personas;
    }

    @Override
    public Boolean ValidateState(State state) {

        List<Object> projects = state.getCode(); // obtener lista de proyectos

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {
            //para cada projecto-rol
            meet = validateProyect(state, i);
            i++;
        }
        return meet;
    }

    public void invalidateState(State state) {
        List<Object> projects = state.getCode();
        ProjectRole projectRole = (ProjectRole) projects.getFirst();
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        RoleWorker roleWorker = roleWorkers.getFirst();

        ProjectRole projectRole2 = (ProjectRole) projects.get(1);
        List<RoleWorker> roleWorkers2 = projectRole2.getRoleWorkers();
        RoleWorker roleWorker2 = roleWorkers2.getFirst();
        roleWorker.getWorkers().set(0, roleWorker2.getWorkers().getFirst());
    }

    public boolean validateProyect(State state, int posProy) {

        List<Object> projects = state.getCode(); // obtener lista de proyectos

        List<PersonEntity> people = personasSolucionNoProyect(projects, posProy);
        boolean meet = true;

        List<PersonEntity> team; // lista con las personas de cada equipo
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

        team = new ArrayList<>();
        int j = 0;
        while (j < roleWorkers.size()) { //para cada rol-persona
            RoleWorker roleWorker = roleWorkers.get(j);

            List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
            aux.addAll(roleWorker.getWorkers());
            aux.addAll(roleWorker.getFixedWorkers());


            team.addAll(aux); //añadir personas que juegan el rol a la lista de personas del equipo actual

            j++;
        }
        int k = 0;
        while (k < team.size() && meet) {  //para cada persona del equipo actual
            PersonEntity worker = team.get(k);

            if (getWorkerOcurrences(people, worker) >= 1) { // verificar si está en un equipo anterior (Da error si llamo al metodo estatico sin pedirlcelo a la clase?)
                meet = false;  // fallo de la restricción
            }
            k++;
        }
        return meet;
    }

    public void repareState(State state, int posProy) throws IOException, ClassNotFoundException {

        List<Object> projects = state.getCode(); // obtener lista de proyectos
        List<PersonEntity> people = personasSolucionNoProyect(projects, posProy);
        List<PersonEntity> team; // lista con las personas de cada equipo
        List<PersonEntity> eliminar = new ArrayList<>();
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

        team = new ArrayList<>();
        int j = 0;
        while (j < roleWorkers.size()) { //para cada rol-persona
            RoleWorker roleWorker = roleWorkers.get(j);
            List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
            aux.addAll(roleWorker.getWorkers());
            aux.addAll(roleWorker.getFixedWorkers());
            team.addAll(aux); //añadir personas que juegan el rol a la lista de personas del equipo actual
            j++;
        }
        int k = 0;
        while (k < team.size()) {  //para cada persona del equipo actual
            PersonEntity worker = team.get(k);
            if (getWorkerOcurrences(people, worker) >= 1) { // verificar si está en un equipo anterior (Da error si llamo al metodo estatico sin pedirlcelo a la clase?)
                eliminar.add(worker);
            }
            k++;
        }

        repareProjectSolution(state, posProy, eliminar);
    }

    public void repareProjectSolution(State state, int posProy, List<PersonEntity> eliminar) throws IOException, ClassNotFoundException {
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random generator = new Random();
        ArrayList<PersonEntity> perSol;
        perSol = TeamFormationOperator.personasSolucion(state.getCode());

        ProjectRole projectRole = (ProjectRole) state.getCode().get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        int j = 0;
        while (j < roleWorkers.size() && !eliminar.isEmpty()) {
            RoleWorker rw = roleWorkers.get(j);
            for (int k = 0; k < rw.getWorkers().size(); k++) {
                PersonEntity worker = rw.getWorkers().get(k);
                for (int i = 0; i < eliminar.size(); i++) {
                    if (worker.getId().equals(eliminar.get(i).getId())) {
                        boolean stop = false;
                        PersonEntity newWorker = new PersonEntity();
                        while (!stop) {
                            int personIndexB = generator.nextInt(codification.getSearchArea().size());
                            newWorker = codification.getSearchArea().get(personIndexB);
                            if (!(perSol.contains(newWorker)))
                                stop = true;
                        }
                        rw.getWorkers().set(k, newWorker);
                        eliminar.remove(i);
                    }
                }

            }
            j++;
        }

    }
}
