package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.pojos.ProjectRoleCompetenceTemplate;
import com.tesis.teamsoft.persistence.entity.AssignedRoleEntity;
import com.tesis.teamsoft.persistence.entity.CycleEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.metaheuristics.restrictions.*;
import problem.definition.Codification;
import problem.definition.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.tesis.teamsoft.config.AlgorithmConfig;

@NoArgsConstructor
@Getter
@Setter
public class TeamFormationCodification extends Codification {

    private List<PersonEntity> searchArea;
    public List<Constrain> restrictions;
    public TeamFormationProblem problem;
    public Double penalEvaluation = 0.0;
    int count_iteration_aleatory_search = 100;

    public TeamFormationCodification(List<Constrain> restrictions, TeamFormationProblem problem, List<PersonEntity> searchArea) {
        super();
        this.restrictions = restrictions;
        this.problem = problem;
        this.searchArea = searchArea;
        this.count_iteration_aleatory_search = AlgorithmConfig.getInt("countIterationAleatorySearch", 100);
    }

    @Override
    public int getAleatoryKey() {
        Random rdm = new Random();
        return rdm.nextInt(problem.getState().getCode().size());
    }

    @Override
    public Object getVariableAleatoryValue(int variable) {
        //Cambiar un trabajador aleatorio de la solucion
        //Escoger un trabajador aleatorio del espacio de soluciones
        Random rdm = new Random();
        int pos = rdm.nextInt(searchArea.size());
        ProjectRole projectRole = (ProjectRole) (problem.getState().getCode().get(variable));
        int roleWorkerPos = rdm.nextInt(projectRole.getRoleWorkers().size());
        RoleWorker roleWorker = projectRole.getRoleWorkers().get(roleWorkerPos);

        //Verificar que el rol no este fijo
        int iteration = 0;
        boolean found = false;
        while (iteration < this.count_iteration_aleatory_search && !found) {
            if (roleWorker.getFixedWorkers().size() != roleWorker.getWorkers().size() && !roleWorker.getWorkers().isEmpty()) {//Significa que el rol no est� fijo
                //Escoger uno de los trabajadores del rol seleccionado aleatoriamente
                int worker = rdm.nextInt(roleWorker.getWorkers().size());
                //Eliminando el trabajador anterior
                roleWorker.getWorkers().remove(worker);
                //Adicionando el nuevo trabajador
                PersonEntity newWorker = searchArea.toArray(new PersonEntity[0])[pos];
                roleWorker.getWorkers().add(newWorker);
                problem.getState().getCode().set(variable, roleWorker);
                //Comprobando que la nueva solucion cumple las restricciones
                if (!validState(problem.getState())) {
                    iteration++;
                } else {
                    found = true;
                }
            } else {
                iteration++;
            }
        }
        return roleWorker;
    }

    @Override
    public int getVariableCount() {
        return problem.getProjects().size();
    }

    public static List<PersonEntity> actualiceTeam(State state, int i) {

        List<Object> projects = state.getCode();
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

        return team;

    }

    @Override
    public boolean validState(State state) {
        return checkIndividualRestrictions(state) && checkTeamRestrictions(state);
    }

    public void repareState(State state) throws IOException, ClassNotFoundException {
        checkIndividualRestrictions2(state);
        checkTeamRestrictions2(state);
        if (!checkIndividualRestrictions(state)) {
            repareIndividualRestrictionsState(state);
        }
        if (!checkTeamRestrictions(state)) {
            repareTeamRestrictionsState(state);
        }
        checkTeamRestrictions2(state);
        checkIndividualRestrictions2(state);
        checkTeamRestrictions2(state);
    }

    /**
     * Chequeo de retricciones individuales uilizando la técnica de rechazo
     *
     * @return true si cumple todas las restricciones
     */
    public boolean checkIndividualRestrictions(State solution) {
        boolean meet = true;
        int i = 0;
        while (i < restrictions.size() && meet) {
            Constrain rest = this.restrictions.get(i);
            if (rest instanceof IsEJ || rest instanceof IsISCO || rest instanceof IsProjectBoss || rest instanceof IsWorkerAssigned || rest instanceof WorkerNotRepeatedInSameRole) {
                meet = rest.ValidateState(solution);
            }
            if (rest instanceof AllCompetitionLevels) {
                ((AllCompetitionLevels) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);
            }
            if (rest instanceof MaximumRoles) {
                ((MaximumRoles) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);
            }

            if (rest instanceof WorkerRemovedFromRole) {
                ((WorkerRemovedFromRole) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);
            }
            if (rest instanceof MaxWorkload) {
                ((MaxWorkload) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);
            }
            if (rest instanceof BossTeamInterest) {
                meet = rest.ValidateState(solution);
            }
            i++;
        }
        return meet;
    }

    public void checkIndividualRestrictions2(State state) {
        int i = 0;
        while (i < restrictions.size()) {

            Constrain rest = this.restrictions.get(i);
            if (rest instanceof IsEJ) {
                rest.ValidateState(state);
            }

            if (rest instanceof IsISCO) {
                rest.ValidateState(state);
            }

            if (rest instanceof IsProjectBoss) {
                rest.ValidateState(state);
            }

            if (rest instanceof IsWorkerAssigned) {
                rest.ValidateState(state);
            }

            if (rest instanceof WorkerNotRepeatedInSameRole) {
                rest.ValidateState(state);
            }

            if (rest instanceof AllCompetitionLevels) {
                ((AllCompetitionLevels) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof MaximumRoles) {
                ((MaximumRoles) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof WorkerRemovedFromRole) {
                ((WorkerRemovedFromRole) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof MaxWorkload) {
                ((MaxWorkload) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof BossTeamInterest) {
                rest.ValidateState(state);
            }
            i++;
        }
    }

    public boolean checkTeamRestrictions(State state) {
        boolean meet = true;
        int i = 0;
        while (i < restrictions.size() && meet) {
            Constrain rest = this.restrictions.get(i);
            if (rest instanceof AllBelbinRoles) {
                meet = rest.ValidateState(state);
            }
            if (rest instanceof AllBelbinCategories) {
                meet = rest.ValidateState(state);
            }
            if (rest instanceof ExistCerebro) {
                ((ExistCerebro) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleRoles) {
                ((IncompatibleRoles) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleSelectedWorkers) {
                ((IncompatibleSelectedWorkers) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleWorkers) {
                meet = rest.ValidateState(state);
            }
            if (rest instanceof isBalanced) {
                ((isBalanced) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof MaxWorkload) {
                ((MaxWorkload) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof DifferentPersonByProject) {
                meet = rest.ValidateState(state);
            }
            if (rest instanceof PersonPerGroupAssigned) {
                ((PersonPerGroupAssigned) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof MinimumRoles) {
                ((MinimumRoles) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            if (rest instanceof BossNeedToBeAssignedToAnotherRole) {
                ((BossNeedToBeAssignedToAnotherRole) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
            }
            i++;
        }
        return meet;
    }

    public void checkTeamRestrictions2(State state) {
        int i = 0;
        while (i < restrictions.size()) {
            Constrain rest = this.restrictions.get(i);
            if (rest instanceof AllBelbinRoles) {
                rest.ValidateState(state);
            }
            if (rest instanceof AllBelbinCategories) {
                rest.ValidateState(state);
            }
            if (rest instanceof ExistCerebro) {
                ((ExistCerebro) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleRoles) {
                ((IncompatibleRoles) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleSelectedWorkers) {
                ((IncompatibleSelectedWorkers) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleWorkers) {
                rest.ValidateState(state);
            }
            if (rest instanceof isBalanced) {
                ((isBalanced) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof DifferentPersonByProject) {
                rest.ValidateState(state);
            }
            if (rest instanceof PersonPerGroupAssigned) {
                ((PersonPerGroupAssigned) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof MinimumRoles) {
                ((MinimumRoles) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            if (rest instanceof BossNeedToBeAssignedToAnotherRole) {
                ((BossNeedToBeAssignedToAnotherRole) rest).setParameters(problem.getParameters());
                rest.ValidateState(state);
            }
            i++;
        }

    }

    public boolean validatingTeamRestrictions(State state) throws IOException, ClassNotFoundException {
        boolean meet = true;
        int i = 0;
        while (i < restrictions.size()) {
            Constrain rest = this.restrictions.get(i);

            if (rest instanceof AllBelbinCategories) {
                meet = rest.ValidateState(state);
                if (meet) {
                    ((AllBelbinCategories) rest).invalidateState(state);
                }
                meet = rest.ValidateState(state);
                if (!meet) {
                    repareTeamRestrictionsState(state);
                }
                meet = rest.ValidateState(state);
            }
            if (rest instanceof IncompatibleSelectedWorkers) {
                ((IncompatibleSelectedWorkers) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
                if (meet) {
                    ((IncompatibleSelectedWorkers) rest).invalidateState(state);
                }
                meet = rest.ValidateState(state);
                if (!meet) {
                    repareTeamRestrictionsState(state);
                }
                meet = rest.ValidateState(state);
            }
            if (rest instanceof MinimumRoles) {
                ((MinimumRoles) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
                if (!meet) {
                    repareTeamRestrictionsState(state);
                }
                meet = rest.ValidateState(state);
            }
            if (rest instanceof BossNeedToBeAssignedToAnotherRole) {
                ((BossNeedToBeAssignedToAnotherRole) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(state);
                if (meet) {
                    ((BossNeedToBeAssignedToAnotherRole) rest).invalidateState(state);
                }
                meet = rest.ValidateState(state);
                if (!meet) {
                    repareTeamRestrictionsState(state);
                }
                meet = rest.ValidateState(state);
            }
            i++;
        }
        return meet;
    }

    public void repareIndividualRestrictionsState(State state) {

        ArrayList<Object> projects = state.getCode();
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);

        ArrayList<PersonEntity> peopleSol = new ArrayList<>(); //lista de todas las presonas de la solucion
        ArrayList<PersonEntity> peopleSol2 = new ArrayList<>(); //lista de todas las presonas de la solucion asignadas antes de un rol y proyecto especifico
        List<PersonEntity> peopleNoJP = new ArrayList<>(); // lista para añadir a todas las personas de la solucion que no son jefes de proyecto
        List<PersonEntity> peopleProyect = new ArrayList<>();  //lista de personas por proyecto

        int i = 0;
        while (i < projects.size()) { //para cada proyecto -rol

            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            int j = 0;
            while (j < roleWorkers.size()) { //para cada rol trabajador
                RoleWorker roleWorker = roleWorkers.get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                peopleProyect.addAll(roleWorker.getWorkers()); //añadir personas a lista de personas;

                int k = 0;
                while (k < aux.size()) { //para cada persona

                    PersonEntity worker = aux.get(k);
                    int a = 0;
                    while (a < restrictions.size()) {
                        boolean meet = true;
                        int in = 0;

                        Constrain rest = this.restrictions.get(a);
                        if (roleWorker.getRole().isBoss()) { // si el rol actual es jefe de proyecto
                            if (rest instanceof IsEJ) {
                                meet = ((IsEJ) rest).validatePerson(worker);
                                while (!meet && in < cantIntentos) {
                                    PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                    meet = ((IsEJ) rest).validatePerson(workerChange);
                                    worker = workerChange;
                                    in++;
                                }

                            }
                            if (rest instanceof IsISCO) {
                                meet = ((IsISCO) rest).validatePerson(worker);
                                while (!meet && in < cantIntentos) {
                                    PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                    meet = ((IsISCO) rest).validatePerson(workerChange);
                                    worker = workerChange;
                                    in++;
                                }
                            }
                            if (rest instanceof IsProjectBoss) {
                                meet = ((IsProjectBoss) rest).validatePerson(peopleSol, worker);
                                if (meet) {
                                    peopleSol.add(worker);
                                }
                                while (!meet && in < cantIntentos) {
                                    PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                    meet = ((IsProjectBoss) rest).validatePerson(peopleSol, workerChange);
                                    worker = workerChange;

                                    if (meet) {
                                        peopleSol.add(worker);
                                    }
                                    in++;
                                }
                            }
                        }

                        if (rest instanceof IsWorkerAssigned) {
                            if (!roleWorker.getRole().isBoss()) { // si el rol actual no es jefe de proyecto
                                peopleNoJP.addAll(aux); //añadir personas que juegan el rol a la lista de personas del equipo actual
                            } else {
                                meet = ((IsWorkerAssigned) rest).validatePerson(peopleNoJP, worker);
                                while (!meet && in < cantIntentos) {
                                    PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                    meet = ((IsWorkerAssigned) rest).validatePerson(peopleNoJP, workerChange);
                                    worker = workerChange;
                                    in++;
                                }
                            }
                        }

                        if (rest instanceof WorkerNotRepeatedInSameRole) {

                            meet = ((WorkerNotRepeatedInSameRole) rest).validatePerson(aux, worker);
                            while (!meet && in < cantIntentos) {
                                PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                aux.clear();
                                aux.addAll(roleWorker.getWorkers());
                                aux.addAll(roleWorker.getFixedWorkers());
                                meet = ((WorkerNotRepeatedInSameRole) rest).validatePerson(aux, workerChange);
                                worker = workerChange;
                                in++;
                            }
                        }

                        if (rest instanceof AllCompetitionLevels) {
                            ((AllCompetitionLevels) rest).setParameters(problem.getParameters());
                            List<ProjectRoleCompetenceTemplate> requirements = ((AllCompetitionLevels) rest).getParameters().getProjects(); //Lista de proyectos configurados por el usuario
                            ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual

                            meet = ((AllCompetitionLevels) rest).validatePerson(projectRequirements, roleWorker, worker);
                            while (!meet && in < cantIntentos) {
                                PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                meet = ((AllCompetitionLevels) rest).validatePerson(projectRequirements, roleWorker, workerChange);
                                worker = workerChange;
                                in++;
                            }
                        }

                        if (rest instanceof MaximumRoles) {
                            ((MaximumRoles) rest).setParameters(problem.getParameters());
                            meet = ((MaximumRoles) rest).validatePerson(worker, peopleProyect);
                            while (!meet && in < cantIntentos) {
                                PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                peopleProyect.clear();
                                peopleProyect.addAll(roleWorker.getWorkers()); //añadir personas a lista de personas;

                                meet = ((MaximumRoles) rest).validatePerson(workerChange, peopleProyect);
                                worker = workerChange;
                                in++;
                            }
                        }
                        if (rest instanceof WorkerRemovedFromRole) {
                            ((WorkerRemovedFromRole) rest).setParameters(problem.getParameters());
                            List<CycleEntity> cycleList = projectRole.getProject().getCycleList();   //obtener ciclos del proyeccto actual

                            meet = ((WorkerRemovedFromRole) rest).validatePerson(worker, roleWorker, cycleList);
                            while (!meet && in < cantIntentos) {
                                PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                meet = ((WorkerRemovedFromRole) rest).validatePerson(workerChange, roleWorker, cycleList);
                                worker = workerChange;
                                in++;
                            }
                        }
                        if (rest instanceof MaxWorkload) {
                            ((MaxWorkload) rest).setParameters(problem.getParameters());
                            float maxWorkload = ((MaxWorkload) rest).getParameters().getMaxRoleLoad().getValue(); //obtener restricción para carga de trabajo

                            float rolLoad = ((MaxWorkload) rest).findWorkLoad(roleWorker.getRole(), projectRole.lastProjectCycle()); //carga de trabajo que implica desempeñar el rol en el proyecto actual
                            meet = ((MaxWorkload) rest).validatePerson(worker, peopleSol2, rolLoad, maxWorkload);

                            while (!meet && in < cantIntentos) {
                                PersonEntity workerChange = changeInvalidPersonSolution(state, worker, i, j);
                                meet = ((MaxWorkload) rest).validatePerson(workerChange, peopleSol2, rolLoad, maxWorkload);
                                worker = workerChange;
                                in++;
                            }
                        }
                        a++;
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
        restoreWorkLoad(state);
        checkIndividualRestrictions2(state);
    }

    public void restoreWorkLoad(State state) {
        ArrayList<Object> projects = state.getCode();
        ProjectRole rp;
        RoleWorker rw;
        for (int i = 0; i < projects.size(); i++) {
            rp = ((ProjectRole) projects.get(i));
            for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                rw = rp.getRoleWorkers().get(j);
                for (int k = 0; k < rw.getWorkers().size(); k++) {
                    ((ProjectRole) state.getCode().get(i)).getRoleWorkers().get(j).getWorkers().get(k).setWorkload((float) 0.0);
                }
            }
        }

    }

    public PersonEntity changeInvalidPersonSolution(State state, PersonEntity invalidWorker, int i, int j) {
        ArrayList<Object> projects = state.getCode();
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random generator = new Random();

        ProjectRole rp;
        RoleWorker rw;
        PersonEntity workerB = new PersonEntity();

        rp = ((ProjectRole) projects.get(i));

        rw = rp.getRoleWorkers().get(j);
        for (int k = 0; k < rw.getWorkers().size(); k++) {

            if (rw.getWorkers().get(k).getId().equals(invalidWorker.getId())) {
                int personIndexB = generator.nextInt(codification.getSearchArea().size());
                workerB = codification.getSearchArea().get(personIndexB);
                ((ProjectRole) state.getCode().get(i)).getRoleWorkers().get(j).getWorkers().set(k, workerB);
            }
        }
        return workerB;
    }

    public void validatingIndividualRestriccion(State solution) throws IOException, ClassNotFoundException {
        ArrayList<Object> projects = solution.getCode();
        ProjectRole projectRole = (ProjectRole) projects.getFirst();
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
        RoleWorker roleWorker = roleWorkers.getFirst();
        PersonEntity worker = roleWorker.getWorkers().getFirst();

        int i = 0;
        while (i < restrictions.size()) {
            boolean meet;

            Constrain rest = this.restrictions.get(i);
            if (rest instanceof IsEJ) {
                meet = rest.ValidateState(solution);
                if (meet) {
                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().getFirst().getRole().setBoss(true);

                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().getFirst().getWorkers().getFirst().getPersonTest().setMbtiType("xxxx");
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);

                }
                meet = rest.ValidateState(solution);
            }

            if (rest instanceof IsISCO) {
                meet = rest.ValidateState(solution);
                if (meet) {
                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().getFirst().getRole().setBoss(true);

                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().getFirst().getWorkers().getFirst().getPersonTest().setIS('I');

                    ((ProjectRole) solution.getCode().get(0)).getRoleWorkers().getFirst().getWorkers().getFirst().getPersonTest().setCO('I');
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);

                }
                meet = rest.ValidateState(solution);

            }

            if (rest instanceof IsProjectBoss) {
                meet = rest.ValidateState(solution);

                if (meet) {
                    //se asigna la persona que está en el primer proyecto con rol de Jefe al segundo proyecto con el mismo rol
                    PersonEntity workerC = ((ProjectRole) solution.getCode().get(0)).getRoleWorkers().getFirst().getWorkers().getFirst();
                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().get(3).getWorkers().set(0, workerC);
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);

            }

            if (rest instanceof IsWorkerAssigned) {
                meet = rest.ValidateState(solution);
                if (meet) {
                    //se asigna la persona q está en el rol Jefe de Proyecto al rol programador y asi esta en ambos roles
                    PersonEntity workerI = ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().get(0).getWorkers().getFirst();
                    ((ProjectRole) solution.getCode().get(1)).getRoleWorkers().get(3).getWorkers().set(0, workerI);
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);

            }

            if (rest instanceof WorkerNotRepeatedInSameRole) {
                meet = rest.ValidateState(solution);
                if (meet) {
                    //se vuelve a añadir la persona a la lista de personas del rol
                    ((ProjectRole) solution.getCode().get(0)).getRoleWorkers().get(0).getWorkers().add(worker);
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);

            }

            if (rest instanceof AllCompetitionLevels) {
                ((AllCompetitionLevels) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);
                if (meet) {
                    //se cambia el valor de una de las competencias por -1 ya que este valor va a ser inferior a cualquier valor minimo
                    ((ProjectRole) solution.getCode().getFirst()).getRoleWorkers().getFirst().getWorkers().getFirst().getCompetenceValueList().getFirst().getLevel().setLevels(-1);
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);

            }
            if (rest instanceof MaximumRoles) {
                ((MaximumRoles) rest).setParameters(problem.getParameters());
                meet = rest.ValidateState(solution);

                if (meet) {
                    //se asigna la persona que ya estaba asignada en un rol de un proyecto a otro rol
                    ((ProjectRole) solution.getCode().getFirst()).getRoleWorkers().get(1).getWorkers().set(0, worker);
                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);

            }
            if (rest instanceof WorkerRemovedFromRole) {
                ((WorkerRemovedFromRole) rest).setParameters(problem.getParameters());

                meet = rest.ValidateState(solution);

                projects = solution.getCode();
                projectRole = (ProjectRole) projects.get(0);
                roleWorkers = projectRole.getRoleWorkers();
                roleWorker = roleWorkers.get(0);
                worker = roleWorker.getWorkers().get(0);

                if (meet) {
                    AssignedRoleEntity assignedRole = new AssignedRoleEntity();

                    Long id = roleWorker.getRole().getId();

                    assignedRole.setId(id);
                    assignedRole.setPerson(worker);
                    assignedRole.setRole(roleWorker.getRole());
                    assignedRole.setStatus(Status.ACTIVE);

                    ((ProjectRole) solution.getCode().get(0)).getProject().getCycleList().get(0).getAssignedRoleList().add(assignedRole);

                }
                meet = rest.ValidateState(solution);
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);
            }
            if (rest instanceof MaxWorkload) {
                ((MaxWorkload) rest).setParameters(problem.getParameters());
                float maxWorkload = ((MaxWorkload) rest).getParameters().getMaxRoleLoad().getValue(); //obtener restricción para carga de trabajo
                float rolLoad = ((MaxWorkload) rest).findWorkLoad(roleWorker.getRole(), projectRole.lastProjectCycle()); //carga de trabajo que implica desempeñar el rol en el proyecto actual

                meet = rest.ValidateState(solution);
                if (meet) {
                    //sobrecargando la carga de la persona
                    ((ProjectRole) solution.getCode().getFirst()).getRoleWorkers().getFirst().getWorkers().getFirst().setWorkload(maxWorkload + rolLoad);
                }
                if (!meet) {
                    repareIndividualRestrictionsState(solution);
                }
                meet = rest.ValidateState(solution);
            }
            i++;
        }
    }

    public void repareTeamRestrictionsState(State state) throws IOException, ClassNotFoundException {

        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles
        int personCount = 0; //contador de personas con rol cerebro
        int i = 0;
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);

        while (i < projects.size()) { //para cada projecto-rol
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

                int k = 0;
                while (k < aux.size()) { // para cada persona
                    PersonEntity worker = aux.get(k);
                    if (worker.getPersonTest() != null) {
                        if (worker.getPersonTest().getCE() != 'I' && worker.getPersonTest().getCE() != 'E') { // si tiene rol cerebro
                            personCount++; //cuento uno
                        }
                    }
                    k++;
                }
                j++;
            }

            int a = 0;
            while (a < restrictions.size()) {
                Constrain rest = this.restrictions.get(a);
                int in = 0;
                boolean meet = true;
                if (rest instanceof AllBelbinRoles) {
                    meet = ((AllBelbinRoles) rest).validateProject(team);
                    while (!meet && in < cantIntentos) {
                        ((AllBelbinRoles) rest).RepareState(state, team, i);
                        team = actualiceTeam(state, i);
                        meet = ((AllBelbinRoles) rest).validateProject(team);
                        in++;
                    }
                }

                if (rest instanceof AllBelbinCategories) {
                    meet = ((AllBelbinCategories) rest).validateProject(team);
                    while (!meet && in < cantIntentos) {
                        ((AllBelbinCategories) rest).RepareState(state, team, i);
                        team = actualiceTeam(state, i);
                        meet = ((AllBelbinCategories) rest).validateProject(team);
                        in++;
                    }
                }
                if (rest instanceof ExistCerebro) {
                    ((ExistCerebro) rest).setParameters(problem.getParameters());
                    int min = ((ExistCerebro) rest).getParameters().getCountBrains();
                    int cantFaltan = min - personCount;
                    while (cantFaltan > 0 && in < cantIntentos) {
                        ((ExistCerebro) rest).repareState(state, cantFaltan);
                        personCount = contarCerebros(state);
                        cantFaltan = min - personCount;
                        in++;
                    }
                }

                if (rest instanceof IncompatibleRoles) {
                    ((IncompatibleRoles) rest).setParameters(problem.getParameters());
                    meet = ((IncompatibleRoles) rest).validateProyect(roleWorkers);
                    while (!meet && in < cantIntentos) {
                        ((IncompatibleRoles) rest).repareState(state, i);
                        meet = ((IncompatibleRoles) rest).validateProyect(roleWorkers);
                        in++;
                    }
                }

                if (rest instanceof IncompatibleSelectedWorkers) {
                    ((IncompatibleSelectedWorkers) rest).setParameters(problem.getParameters());
                    meet = ((IncompatibleSelectedWorkers) rest).validateProyect(state, i);
                    while (!meet && in < cantIntentos) {
                        ((IncompatibleSelectedWorkers) rest).repareState(state, i);
                        meet = ((IncompatibleSelectedWorkers) rest).validateProyect(state, i);
                        in++;
                    }
                }

                if (rest instanceof IncompatibleWorkers) {
                    meet = ((IncompatibleWorkers) rest).validateProyect(state, i);
                    while (!meet && in < cantIntentos) {
                        ((IncompatibleWorkers) rest).repareState(state, i);
                        meet = ((IncompatibleWorkers) rest).validateProyect(state, i);
                        in++;

                    }
                }

                if (rest instanceof isBalanced) {
                    ((isBalanced) rest).setParameters(problem.getParameters());
                    meet = ((isBalanced) rest).validateProyect(team);
                    while (!meet && in < cantIntentos) {
                        ((isBalanced) rest).repareState(state, team, i);
                        team = actualiceTeam(state, i);
                        meet = ((isBalanced) rest).validateProyect(team);
                        in++;
                    }
                }

                if (rest instanceof DifferentPersonByProject) {
                    meet = ((DifferentPersonByProject) rest).validateProyect(state, i);
                    while (!meet && in < cantIntentos) {
                        ((DifferentPersonByProject) rest).repareState(state, i);
                        meet = ((DifferentPersonByProject) rest).validateProyect(state, i);
                        in++;
                    }
                }

                if (rest instanceof PersonPerGroupAssigned) {
                    ((PersonPerGroupAssigned) rest).setParameters(problem.getParameters());
                    meet = ((PersonPerGroupAssigned) rest).validateProyect(state, i);
                    while (!meet && in < cantIntentos) {
                        ((PersonPerGroupAssigned) rest).repareState(state, i);
                        meet = ((PersonPerGroupAssigned) rest).validateProyect(state, i);
                        in++;
                    }
                }
                a++;
            }
            i++;
        }
    }

    public int contarCerebros(State state) {
        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles
        int personCount = 0;
        for (Object project : projects) { //para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) project;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            for (RoleWorker roleWorker : roleWorkers) { //para cada rol-persona
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(roleWorker.getWorkers());
                aux.addAll(roleWorker.getFixedWorkers());
                for (PersonEntity worker : aux) { // para cada persona
                    if (worker.getPersonTest() != null) {
                        if (worker.getPersonTest().getCE() != 'I' && worker.getPersonTest().getCE() != 'E') { // si tiene rol cerebro
                            personCount++; //cuento uno
                        }

                    }
                }

            }
        }
        return personCount;
    }

    public boolean isTeamComplete(State state) {
        boolean complete = true;
        int total_projects = state.getCode().size();
        int i, j = 0;
        while (j < total_projects && complete) {
            i = 0;
            ProjectRole projectRole = (ProjectRole) state.getCode().get(j);
            while (i < projectRole.getRoleWorkers().size() && complete) {
                RoleWorker roleWorker = projectRole.getRoleWorkers().get(i);
                if (roleWorker.getNeededWorkers() > 0) {
                    complete = false;
                }
                i++;
            }
            j++;
        }
        return complete;
    }
}
