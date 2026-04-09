package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.config.AlgorithmConfig;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.pojos.SelectedRoleIncompatibility;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationCodification;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Verificar que no juegue roles incompatibles dentro de un mismo proyecto
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IncompatibleRoles extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {


        List<Object> projects = state.getCode();  //lista de projectos - roles

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {  // para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            meet = validateProyect(roleWorkers);

            i++;
        }
        return meet;
    }


    public boolean validateProyect(List<RoleWorker> roleWorkers) {

        List<SelectedRoleIncompatibility> riL = parameters.getSRI();
        List<Long> personRolA;
        List<Long> personRolB;

        boolean meet = true;
        for (int l = 0; l < riL.size() && meet; l++) { //por cada par de roles incopatibles (hacerlo con un while mientras meet = true)

            SelectedRoleIncompatibility incRol = riL.get(l);

            personRolA = new ArrayList<>();
            personRolB = new ArrayList<>();

            int j = 0;
            while (j < roleWorkers.size()) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);

                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());

                int k = 0;
                while (k < aux.size()) { // para cada persona
                    PersonEntity worker = aux.get(k);

                    if (roleWorker.getRole().getId().equals(incRol.getRoleAFk().getId())) {
                        personRolA.add(worker.getId());
                    }
                    if (roleWorker.getRole().getId().equals(incRol.getRoleBFk().getId())) {
                        personRolB.add(worker.getId());
                    }
                    k++;
                }
                j++;
            }
            meet = checkIncompatibilities(personRolA, personRolB);
        }
        return meet;
    }

    public void repareState(State state, int posProy) {

        List<SelectedRoleIncompatibility> riL = parameters.getSRI();
        List<Long> personRolA = new ArrayList<>();
        List<Long> personRolB = new ArrayList<>();
        Random generator = new Random();
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);
        boolean meet;

        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        List<Object> projects = state.getCode();  //lista de projectos - roles
        ProjectRole projectRole = (ProjectRole) projects.get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

        for (SelectedRoleIncompatibility incRol : riL) { //por cada par de roles incopatibles
            personRolA.clear();
            personRolB.clear();
            int j = 0;
            while (j < roleWorkers.size()) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                int k = 0;
                while (k < aux.size()) { // para cada persona
                    PersonEntity worker = aux.get(k);
                    if (roleWorker.getRole().getId().equals(incRol.getRoleAFk().getId())) {
                        personRolA.add(worker.getId());
                    }
                    if (roleWorker.getRole().getId().equals(incRol.getRoleBFk().getId())) {
                        personRolB.add(worker.getId());
                    }
                    k++;
                }
                j++;
            }
            meet = checkIncompatibilities(personRolA, personRolB);
            if (!meet) {
                List<Long> persInc = returnIncompatibilities(personRolA, personRolB);
                ArrayList<PersonEntity> candidatos = new ArrayList<>();
                int a = 0;
                while (a < persInc.size() && candidatos.size() < persInc.size()) {
                    PersonEntity workerB;
                    List<Long> idWorkersB = new ArrayList<>();
                    int personIndex = generator.nextInt(codification.getSearchArea().size());
                    workerB = codification.getSearchArea().get(personIndex);
                    idWorkersB.add(workerB.getId());
                    meet = checkIncompatibilities(personRolA, idWorkersB);
                    int contadorIntentos = 0;
                    while (!meet && contadorIntentos < cantIntentos) {
                        personIndex = generator.nextInt(codification.getSearchArea().size());
                        workerB = codification.getSearchArea().get(personIndex);
                        if (!idWorkersB.contains(workerB.getId())) idWorkersB.add(workerB.getId());
                        meet = checkIncompatibilities(personRolA, idWorkersB);
                        contadorIntentos++;
                    }
                    if (meet) {
                        candidatos.add(workerB);
                    }
                    a++;
                }
                repareIncRols(state, posProy, incRol.getRoleAFk().getId(), persInc, candidatos);
            }
        }

    }

    /**
     * @param personRolA
     * @param personRolB
     * @return true si ninguna persona juega roles incompatibles
     */
    public List<Long> returnIncompatibilities(List<Long> personRolA, List<Long> personRolB) {

        List<Long> persInc = new ArrayList<>();

        int i = 0;
        while (i < personRolA.size()) {
            if (personRolB.contains(personRolA.get(i))) {
                persInc.add(personRolA.get(i));
            }
            i++;
        }
        return persInc;
    }

    public boolean checkIncompatibilities(List<Long> personRolA, List<Long> personRolB) {
        boolean meet = true;
        int i = 0;
        while (i < personRolA.size() && meet) {
            if (personRolB.contains(personRolA.get(i))) {
                meet = false;
            }
            i++;
        }
        return meet;
    }


    void repareIncRols(State state, int posProy, Long idRol, List<Long> persInc, ArrayList<PersonEntity> candidatos) {
        int c = 0;
        ProjectRole projectRole = (ProjectRole) state.getCode().get(posProy);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        int j = 0;
        while (j < roleWorkers.size() && !candidatos.isEmpty()) {
            RoleWorker rw = roleWorkers.get(j);

            if (rw.getRole().getId().equals(idRol)) {
                int k = 0;
                while (k < rw.getWorkers().size()) {
                    PersonEntity worker = rw.getWorkers().get(k);
                    if (persInc.contains(worker.getId())) {
                        ((ProjectRole) state.getCode().get(posProy)).getRoleWorkers().get(j).getWorkers().set(k, candidatos.remove(c));
                        c++;
                    }
                    k++;
                }
            }
            j++;
        }
    }
}
