package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.config.AlgorithmConfig;
import com.tesis.teamsoft.metaheuristics.auxiliary.TeamFormationProblem;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import metaheurictics.strategy.Strategy;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.metaheuristics.auxiliary.*;
import com.tesis.teamsoft.metaheuristics.auxiliary.test.BelbinCategoryRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.test.CompetenceProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.test.CompetenceRoleWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.test.CompetentWorker;
import problem.definition.Operator;
import problem.definition.State;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author G1lb3rt & zucelys & jpinas
 */
public class TeamFormationOperator extends Operator {

    public TeamFormationOperator() {
    }

    @Override
    public List<State> generateRandomState(Integer operatornumber) {

        List<State> toReturn = new ArrayList<>();
        boolean invalidState = true;
        int count = 0;
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);

        int posibleValidateNumber = AlgorithmConfig.getInt("posibleValidateNumber", 20);
        while (invalidState && count < posibleValidateNumber) {

            switch (((TeamFormationProblem) Strategy.getStrategy().getProblem()).getParameters().getConfFormMode()) {//Seleccion de metodo de formacion

                case 1:
                    try {
                        toReturn.add(buildteam());//contruir secuencialmente solucion inicial que cumpla las restricciones
                    } catch (IOException e) {
                        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                    break;

                case 2, 4:
                    toReturn.add(buildTeamSimultaneously());//contruir simultaneamente solucion inicial que cumpla las restricciones
                    break;

                case 3:
                    try {
                        toReturn.add(buildteam());//contruir secuencialmente solucion inicial que cumpla las restricciones
                    } catch (IOException e) {
                        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
                        throw new RuntimeException(e.getLocalizedMessage());
                    }
                    break;
            }
            count++;
            TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
            invalidState = repareState(toReturn, cantIntentos, count, codification);
        }
        return toReturn;
    }

    @Override
    public List<State> generatedNewState(State currentState, Integer operatornumber) {

        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        int cantIntentos = AlgorithmConfig.getInt("cantIntentos", 50);
        List<State> toReturn = new ArrayList<>();
        boolean invalidState = true;
        int count = 0;

        int posibleValidateNumber = AlgorithmConfig.getInt("posibleValidateNumber", 20);
        while (invalidState && count < posibleValidateNumber) {
            ProjectRoleState prState = new ProjectRoleState(currentState.getCode(), null, currentState.getTypeGenerator());
            ArrayList<Object> projects = prState.getCode();
            Random generator = new SecureRandom();
            int operator = AlgorithmConfig.getInt("mutationOperatorOpc", 0);

            if (!(operator >= 0 && operator <= 4)) { // si no esta en este intervalo
                operator = generator.nextInt(5); // dame uno aleatorio
            }


            ProjectRole proyectA = tomarProyectoAleatorio(projects, generator);
            ProjectRole proyectB = tomarProyectoAleatorio(projects, generator);

            List<RoleWorker> roleWorkersA = proyectA.getRoleWorkers();
            List<RoleWorker> roleWorkersB = proyectB.getRoleWorkers();

            //OJO por defecto para permutacion entre dos proyectos
            if (projects.size() > 1) {
                while (proyectA.getProject().getId().equals(proyectB.getProject().getId())) {
                    proyectB = tomarProyectoAleatorio(projects, generator);
                    roleWorkersB = proyectB.getRoleWorkers();
                }
            }
           
            List<RoleWorker> roleWorkerAuxA = roleWorkerWithOutFixed(roleWorkersA);
            List<RoleWorker> roleWorkerAuxB = roleWorkerWithOutFixed(roleWorkersB);
            if(!roleWorkerAuxA.isEmpty()){
                RoleWorker roleWorkerA = tomarRolAlatorio(roleWorkerAuxA, generator);
                
                switch (operator) {
                case 0:
                    MutationOperator.OperadorMutacion0(roleWorkerA, codification, generator);
                    break;
                case 1:
                    MutationOperator.OperadorMutacion1(roleWorkerA, roleWorkerAuxA, generator);
                    break;
                case 2:
                    MutationOperator.OperadorMutacion2(roleWorkerA, roleWorkerAuxB, generator);
                    break;
                case 3:
                    MutationOperator.performPermutationBoss(projects, codification, generator);
                    break;
                case 4:
                    MutationOperator.performExternalSubtitution(projects, codification, generator);
                    break;
                }
               toReturn.add(prState);
               count++;
               invalidState = repareState(toReturn, cantIntentos, count, codification);
            }
            else{
                if(projects.size() > 1){
                    count++;                   
                }
                else{
                    toReturn.add(currentState);
                    invalidState = false;
                }
            }
 
        }
        return toReturn;
    }
    
    /*
     para no tener en cuenta los roles que tienen personas fijadas
    */

    public List<RoleWorker> roleWorkerWithOutFixed(List<RoleWorker> roleWorkers){
        List<RoleWorker> refinedList = new ArrayList<RoleWorker>();
       
        for(int i = 0; i < roleWorkers.size(); i++){
          
            if(roleWorkers.get(i).getFixedWorkers().isEmpty()){
                
                refinedList.add(roleWorkers.get(i));
            }
        }
        return refinedList;
    }
    @Override
    public List<State> generateNewStateByCrossover(State state, State state2) {
        Random random = new Random();
        List<State> toReturn = new ArrayList<>();

        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        int cant = AlgorithmConfig.getInt("cantIntentos", 50);
        int maxOperatorCruce = AlgorithmConfig.getInt("operatorCruce", 8);
        int operator = random.nextInt(maxOperatorCruce);
        boolean invalidState = true;

        if (!(operator <= 7)) { // si no esta en este intervalo
            operator = random.nextInt(8); // dame uno aleatorio
        }


        int count = 0;
        ProjectRoleState prState1 = new ProjectRoleState(state.getCode(), null, state.getTypeGenerator());
        ArrayList<Object> projects = prState1.getCode();
        ProjectRoleState prState2 = new ProjectRoleState(state2.getCode(), null, state2.getTypeGenerator());
        ArrayList<Object> projects2 = prState2.getCode();

        int posibleValidateNumber = AlgorithmConfig.getInt("posibleValidateNumber", 20);
        while (invalidState && count < posibleValidateNumber) {
            try {
                ArrayList<ArrayList<PersonEntity>> perSols = Cruzamiento7y8.OperadorCruzamiento7y8(projects, projects2, operator);
                ArrayList<PersonEntity> pers1 = personasSolucion(projects);
                ArrayList<PersonEntity> pers2 = personasSolucion(projects2);

                int posPers = 0;
                if (operator == 0) {
                    prState1.setCode(Cruzamiento1.OperadorCruzamiento1(projects, projects2).get(0));
                    prState2.setCode(Cruzamiento1.OperadorCruzamiento1(projects, projects2).get(1));
                } else {
                    for (int i = 0; i < projects.size(); i++) {
                        if (operator == 1) {
                            projects.set(i, Cruzamiento2.OperadorCruzamiento2(((ProjectRole) projects.get(i)), ((ProjectRole) projects2.get(i))).get(0));
                            projects2.set(i, Cruzamiento2.OperadorCruzamiento2(((ProjectRole) projects.get(i)), ((ProjectRole) projects2.get(i))).get(1));
                        } else {
                            ArrayList<RoleWorker> proyRoles1 = (ArrayList<RoleWorker>) ((ProjectRole) projects.get(i)).getRoleWorkers();
                            ArrayList<RoleWorker> proyRoles2 = (ArrayList<RoleWorker>) ((ProjectRole) projects2.get(i)).getRoleWorkers();
                            ArrayList<PersonEntity> persProy = new ArrayList<>();

                            int cantRol = ((ProjectRole) projects.get(i)).getRoleWorkers().size();
                            for (int j = 0; j < cantRol; j++) {
                                RoleWorker temRoleWorker = proyRoles1.get(j);
                                RoleWorker temRoleWorker2 = proyRoles2.get(j);

                                Long idRol = ((ProjectRole) projects.get(i)).getRoleWorkers().get(j).getRole().getId();
                                int cantPer = proyRoles1.get(j).getWorkers().size();
                                for (int k = 0; k < cantPer; k++) {
                                    PersonEntity temPersona = new PersonEntity();
                                    PersonEntity temPersona2 = new PersonEntity();
                                    int numVec = random.nextInt(2);

                                    switch (operator) {
                                        case 2:
                                            temPersona = Cruzamiento3.OperadorCruzamiento3(proyRoles1, proyRoles2, j, k, numVec).get(0);
                                            temPersona2 = Cruzamiento3.OperadorCruzamiento3(proyRoles1, proyRoles2, j, k, numVec).get(1);
                                            break;
                                        case 3:
                                            temPersona = Cruzamiento4.OperadorCruzamiento4(pers1, pers2, idRol, numVec);
                                            break;
                                        case 4:
                                            temPersona = Cruzamiento5.OperadorCruzamiento5(pers1, pers2, idRol, persProy, numVec);
                                            persProy.add(temPersona);
                                            break;
                                        case 5:
                                            temPersona = Cruzamiento6.OperadorCruzamiento6(proyRoles1.get(j).getWorkers().get(k), proyRoles2.get(j).getWorkers().get(k), idRol, persProy);
                                            persProy.add(temPersona);
                                            break;
                                        case 6, 7:
                                            temPersona = perSols.get(0).get(posPers);
                                            temPersona2 = perSols.get(1).get(posPers);
                                            posPers++;
                                            break;
                                    }
                                    temRoleWorker.getWorkers().set(k, temPersona);
                                    if (operator == 2 || operator == 6 || operator == 7) {
                                        temRoleWorker2.getWorkers().set(k, temPersona2);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(TeamFormationOperator.class.getName()).log(Level.SEVERE, null, ex);
            }
            toReturn.add(prState1);
            boolean stop2 = true;

            if (operator == 0 || operator == 1 || operator == 2 || operator == 6 || operator == 7) {
                toReturn.add(prState2);
                stop2 = codification.validState(toReturn.get(1));
            }
            count++;
            boolean stop1 = codification.validState(toReturn.get(0));

            int i = 0;
            while ((!stop1 || !stop2) && i < cant) {
                try {
                    if (!stop1) {
                        codification.repareState(toReturn.getFirst());
                        stop1 = codification.validState(toReturn.getFirst());
                    }
                    if (!stop2) {
                        codification.repareState(toReturn.get(1));
                        stop2 = codification.validState(toReturn.get(1));
                    }
                    i++;
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(TeamFormationOperator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if ((stop1 && stop2)) {
                System.out.println("Estado Válido");
                invalidState = false;
            } else if (count < posibleValidateNumber) {
                System.out.println("Estado Inválido");
                toReturn.clear();
            }
            if (count >= posibleValidateNumber) {
                int iterations = AlgorithmConfig.getInt("iterations", 100);
                Strategy.getStrategy().setCountCurrent(iterations);
            }
        }
        return toReturn;

    }

    /**
     * Construir un equipo de forma aleatoria que cumpla con las restricciones
     * impuestas por el usuario
     *
     * @return ProjectRoleState
     */
    public ProjectRoleState buildteam() throws IOException {
        ProjectRoleState state = new ProjectRoleState(Strategy.getStrategy().getProblem().getState().getCode(), null, Strategy.getStrategy().getProblem().getState().getTypeGenerator());
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random rdm = new Random();
        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles

        int initialSolution = AlgorithmConfig.getInt("initialSolutionConf", 4);

        if (initialSolution == 0) {
            ArrayList<BelbinCategoryRole> workersMultiList = RestrictSearchAreaBelbinRoles(state, codification);

            for (Object project : projects) { //para cada proyecto
                ProjectRole projectRole = (ProjectRole) project;
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                for (RoleWorker roleWorker : roleWorkers) { //para cada rol-persona
                    Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto
                    String projectBelbinCategories = checkBelbinCategories(projectRole);
                    List<PersonEntity> belbinCat;

                    if (projectBelbinCategories.charAt(2) == '0') {
                        belbinCat = workersMultiList.get(2).getCategoryWorkers();
                    } else if (projectBelbinCategories.charAt(1) == '0') {
                        belbinCat = workersMultiList.get(1).getCategoryWorkers();
                    } else if (projectBelbinCategories.charAt(0) == '0') {
                        belbinCat = workersMultiList.getFirst().getCategoryWorkers();
                    } else {
                        belbinCat = workersMultiList.get(rdm.nextInt(workersMultiList.size())).getCategoryWorkers();
                    }

                    int count = 0;
                    int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                    while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol

                        if (projectBelbinCategories.contains("0")) {
                            int pIndex = rdm.nextInt(belbinCat.size()); //indice de persona aleatorio
                            //WORKING FALTAN LOS PARAMETROS (ESTOY EN VARIANTE ALEATORIA)
                            PersonEntity chosenPerson = belbinCat.get(pIndex);
                            roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista
                        } else {
                            int pIndex = rdm.nextInt(codification.getSearchArea().size()); //indice de persona aleatorio
                            PersonEntity chosenPerson = ((List<PersonEntity>) codification.getSearchArea()).get(pIndex);
                            roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista
                        }

                        if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                            roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                            needs--;
                        } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                            count++;
                            if (count != limitPersonTries) {
                                roleWorker.getWorkers().removeLast();
                            }
                        }
                    }
                }
            }
        } else if (initialSolution == 1) {
            ArrayList<CompetenceProjectRole> workersMultiList = RestrictSearchAreaCompetences(state, codification);
            for (int i = 0; i < projects.size(); i++) { //para cada proyecto
                ProjectRole projectRole = (ProjectRole) projects.get(i);
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
                CompetenceProjectRole compProjectRole = workersMultiList.get(i);
                List<CompetenceRoleWorker> compRoleWorkers = compProjectRole.getRoleWorkers();

                for (int j = 0; j < roleWorkers.size(); j++) { //para cada rol-persona
                    RoleWorker roleWorker = roleWorkers.get(j);
                    Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto
                    CompetenceRoleWorker compRoleWorker = compRoleWorkers.get(j);

                    int count = 0;
                    int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                    while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                        int pIndex = rdm.nextInt(compRoleWorker.getWorkers().size()); //indice de persona aleatorio

                        //WORKING FALTAN LOS PARAMETROS (ESTOY EN VARIANTE ALEATORIA)
                        PersonEntity chosenPerson = compRoleWorker.getWorkers().get(pIndex).getWorker();
                        roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                        if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                            roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                            needs--;
                        } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                            count++;
                            if (count != limitPersonTries) {
                                roleWorker.getWorkers().removeLast();
                            }
                        }
                    }
                }
            }
        } else if (initialSolution == 2) {
            /*esta construccion se debe realizar para cuando existe un minimo de roles
             * a cumplir en el proyecto.*/
            if (codification.getProblem().getParameters().isMinimunRoles() &&
                    codification.getProblem().getParameters().getMinimumRole() == 2) {
//                int minRole = codification.getProblem().getParameters().getMinimumRole();

                List<PersonEntity> allWorkers = codification.getSearchArea(); // workers disponibles
                for (Object objectProject : projects) { //para cada proyecto
                    ProjectRole actualProject = (ProjectRole) objectProject;
                    RoleWorker projectBoss = actualProject.getProjectBoss();

                    List<PersonEntity> assignedWorkers = new LinkedList<>(); // workers asignados a la cant de minRol
                    List<RoleWorker> assignedRoleWorkers = new LinkedList<>();
                    List<RoleWorker> allProjectRoles = actualProject.getRoleWorkers();

                    /*mientras existan roles disponibles y trabajadores disponibles*/
                    while (existAvailableRoles(allProjectRoles, assignedRoleWorkers)) {
                        PersonEntity workerToAssign = getRandomNotAssignedWorker(allWorkers, assignedWorkers);
                        if (workerToAssign != null) {
                            RoleWorker roleToAssign = getRandomAvailableRole(allProjectRoles, assignedRoleWorkers);

                            // en este caso no va a ser null xq ya se pregunto en la condicion del while antes de entrar
                            assert roleToAssign != null;
                            RoleWorker compatibleRole = getRandomCompatibleRole(
                                    actualProject.getRoleWorkers(), roleToAssign, assignedRoleWorkers
                            );
                            if (compatibleRole != null) { // si hay rol compatible disponible
                                // se asigna el worker a los roles
                                roleToAssign.getWorkers().add(workerToAssign);
                                compatibleRole.getWorkers().add(workerToAssign);

                                // se añade como worker asignado
                                assignedWorkers.add(workerToAssign);

                                //se verifica que los roles les queden personas, si no, se añaden a la lista de roles asignados
                                if (roleToAssign.getWorkers().size() == roleToAssign.getNeededWorkers()) {
                                    assignedRoleWorkers.add(roleToAssign);
                                }
                                if (compatibleRole.getWorkers().size() == compatibleRole.getNeededWorkers()) {
                                    assignedRoleWorkers.add(compatibleRole);
                                }
                            } else {
                                throw new RuntimeException("Not exist supported roles " + roleToAssign.getRole().getRoleName());
                            }
                        } else {
                            throw new RuntimeException("Not exist supported roles " + actualProject.getProject().getProjectName());
                        }
                    }
                    /*para no calcular otra vez si no hay roles disponibles es mejor preguntar si
                     * todavia las condiciones de que existen workers y roles compatibles todavia estan en true, si
                     * estan en true fue que la 1ra condicion no se cumplio, por lo que se acabaron los roles
                     * disponibles*/
                    /*obtengo una de las personas asignadas de forma aleatoria y la asigno al rol lider*/
                    projectBoss.getWorkers().add(assignedWorkers.get(rdm.nextInt(assignedWorkers.size())));
                }
            } else {
                throw new RuntimeException("Error initial solution minimum roles");
            }
        } else if (initialSolution == 3) {
            /*construye la solucion de forma aleatoria y al final asigna al jefe una personas aleatoria*/
            for (Object project : projects) { //para cada proyecto
                ProjectRole projectRole = (ProjectRole) project;
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
                RoleWorker boss = projectRole.getProjectBoss();
                roleWorkers.remove(boss);

                for (RoleWorker roleWorker : roleWorkers) { //para cada rol-persona
                    Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto

                    int count = 0;
                    int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                    while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                        int pIndex = rdm.nextInt(codification.getSearchArea().size()); //indice de persona aleatorio
                        PersonEntity chosenPerson = codification.getSearchArea().get(pIndex);
                        roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                        if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                            roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                            needs--;
                        } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                            count++;
                            if (count != limitPersonTries) {
                                roleWorker.getWorkers().remove(roleWorker.getWorkers().size() - 1);
                            }
                        }
                    }
                }
                RoleWorker roleWorker = getRandomRoleWorker(roleWorkers);
                PersonEntity worker = getRandomWorker(roleWorker.getWorkers());
                boss.getWorkers().add(worker);
                roleWorkers.add(boss);
            }
        } else {
            for (Object project : projects) { //para cada proyecto
                ProjectRole projectRole = (ProjectRole) project;
                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                for (RoleWorker roleWorker : roleWorkers) { //para cada rol-persona
                    Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto

                    int count = 0;
                    int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                    while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                        int pIndex = rdm.nextInt(codification.getSearchArea().size()); //indice de persona aleatorio
                        PersonEntity chosenPerson = codification.getSearchArea().get(pIndex);
                        roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                        if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                            roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                            needs--;
                        } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                            count++;
                            if (count != limitPersonTries) {
                                roleWorker.getWorkers().removeLast();
                            }
                        }
                    }
                }
            }
        }
        return state;
    }

    /**
     * Construir equipos de forma aleatoria y simultanea que cumplan con las
     * restricciones impuestas por el usuario
     *
     * @return
     */
    public ProjectRoleState buildTeamSimultaneously() {
        ProjectRoleState state = new ProjectRoleState(Strategy.getStrategy().getProblem().getState().getCode(), null, Strategy.getStrategy().getProblem().getState().getTypeGenerator());
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        Random rdm = new Random();
        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles
        ArrayList<Long> ids = new ArrayList<>(projects.size());

        for (Object project : projects) {
            Long id = ((ProjectRole) project).getProject().getId();
            ids.add(id);
        }

        int role = 0;

        int initialSolution = AlgorithmConfig.getInt("initialSolutionConf", 4);

        if (initialSolution == 0) {
            ArrayList<BelbinCategoryRole> workersMultiList = RestrictSearchAreaBelbinRoles(state, codification);
//            try {
//                SaveTxt.writeBelbinWorkersList(workersMultiList);
//            } catch (IOException ex) {
//                Logger.getLogger(TeamFormationOperator.class.getName()).log(Level.SEVERE, null, ex);
//            }

            while (!ids.isEmpty()) { //Mientras queden proyectos en la lista por llenar
                for (Object project : projects) { //para cada proyecto
                    ProjectRole projectRole = (ProjectRole) project;

                    if (ids.contains(projectRole.getProject().getId())) {
                        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                        if (role < roleWorkers.size()) {
                            RoleWorker roleWorker = roleWorkers.get(role);
                            Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto

                            String projectBelbinCategories = checkBelbinCategories(projectRole);
                            List<PersonEntity> belbinCat;

                            if (projectBelbinCategories.charAt(2) == '0') {
                                belbinCat = workersMultiList.get(2).getCategoryWorkers();
                            } else if (projectBelbinCategories.charAt(1) == '0') {
                                belbinCat = workersMultiList.get(1).getCategoryWorkers();
                            } else if (projectBelbinCategories.charAt(0) == '0') {
                                belbinCat = workersMultiList.getFirst().getCategoryWorkers();
                            } else {
                                belbinCat = workersMultiList.get(rdm.nextInt(workersMultiList.size())).getCategoryWorkers();
                            }

                            int count = 0;
                            int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                            while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                                int pIndex = rdm.nextInt(belbinCat.size()); //indice de persona aleatorio

                                //WORKING FALTAN LOS PARAMETROS (ESTOY EN VARIANTE ALEATORIA)
                                PersonEntity chosenPerson = belbinCat.get(pIndex);
                                roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                                if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                                    roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                                    needs--;
                                } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                                    count++;
                                    if (count != limitPersonTries) {
                                        roleWorker.getWorkers().removeLast();
                                    }
                                }
                            }
                        } else {
                            ids.remove(projectRole.getProject().getId());
                        }
                    }
                }
                role++;
            }
        } else if (initialSolution == 1) {
            ArrayList<CompetenceProjectRole> workersMultiList = RestrictSearchAreaCompetences(state, codification);

//            try {
//                SaveTxt.writeCompetenceWorkersList(workersMultiList);
//            } catch (IOException ex) {
//                Logger.getLogger(TeamFormationOperator.class.getName()).log(Level.SEVERE, null, ex);
//            }

            while (!ids.isEmpty()) { //Mientras queden proyectos en la lista por llenar
                for (int i = 0; i < projects.size(); i++) { //para cada proyecto
                    ProjectRole projectRole = (ProjectRole) projects.get(i);
                    CompetenceProjectRole compProjectRole = workersMultiList.get(i);

                    if (ids.contains(projectRole.getProject().getId())) {
                        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
                        List<CompetenceRoleWorker> compRoleWorkers = compProjectRole.getRoleWorkers();

                        if (role < roleWorkers.size()) {
                            RoleWorker roleWorker = roleWorkers.get(role);
                            Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto
                            CompetenceRoleWorker compRoleWorker = compRoleWorkers.get(role);

                            int count = 0;
                            int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                            while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                                int pIndex = rdm.nextInt(compRoleWorker.getWorkers().size()); //indice de persona aleatorio

                                //WORKING FALTAN LOS PARAMETROS (ESTOY EN VARIANTE ALEATORIA)
                                PersonEntity chosenPerson = compRoleWorker.getWorkers().get(pIndex).getWorker();
                                roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                                if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                                    roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                                    needs--;
                                } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                                    count++;
                                    if (count != limitPersonTries) {
                                        roleWorker.getWorkers().removeLast();
                                    }
                                }
                            }
                        } else {
                            ids.remove(projectRole.getProject().getId());
                        }
                    }
                }
                role++;
            }
        } else {
            while (!ids.isEmpty()) { //Mientras queden proyectos en la lista por llenar
                for (Object project : projects) { //para cada proyecto
                    ProjectRole projectRole = (ProjectRole) project;

                    if (ids.contains(projectRole.getProject().getId())) {
                        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

                        if (role < roleWorkers.size()) {
                            RoleWorker roleWorker = roleWorkers.get(role);
                            Long needs = roleWorker.getNeededWorkers(); //obtener cantidad de pesonas necesarias para desarrollar el proyecto

                            int count = 0;
                            int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);
                            while (needs > 0 && count < limitPersonTries) { // intentar una cantidad (limitPersonTries) de veces, poner una persona en un rol
                                int pIndex = rdm.nextInt(codification.getSearchArea().size()); //indice de persona aleatorio
                                PersonEntity chosenPerson = codification.getSearchArea().get(pIndex);
                                roleWorker.getWorkers().add(chosenPerson); //añadir al final de la lista

                                if (codification.checkIndividualRestrictions(state)) { // si puede desempeñar el rol, actualizar cant. personas necesarioas
                                    roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() == 0 ? 0 : roleWorker.getNeededWorkers() - 1);
                                    needs--;
                                } else {  // si la persona NO puede desempeñar el rol, quitarla de la lista, contar un intento
                                    count++;
                                    if (count != limitPersonTries) {
                                        roleWorker.getWorkers().removeLast();
                                    }
                                }
                            }
                        } else {
                            ids.remove(projectRole.getProject().getId());
                        }
                    }
                }
                role++;
            }
        }
        return state;
    }

    //////////////////////////////////////////////////////////////////////////////
    /////////METODOS PARA LA FORMACION DEL EQUIPO
    //////////////////////////////////////////////////////////////////////////////

    ///////////////////Métodos necesarios para operadores de mutación y cruzamiento///////////////////////////////////////////////
    public static ProjectRole tomarProyectoAleatorio(ArrayList<Object> projects, Random generator) {
        return ((ProjectRole) projects.get(generator.nextInt(projects.size())));
    }

    public static RoleWorker tomarRolAlatorio(List<RoleWorker> roleWorkers, Random generator) {
        int rIndex = generator.nextInt(roleWorkers.size());

        while (roleWorkers.get(rIndex).getWorkers().isEmpty()) { //Validar que no se escojan roles fijados
            rIndex = generator.nextInt(roleWorkers.size());
        }
        return roleWorkers.get(rIndex);
    }

    public static PersonEntity tomarPersonaAlatoria(RoleWorker roleWorker, Random generator) {
        int personIndex = generator.nextInt(roleWorker.getWorkers().size());
        return roleWorker.getWorkers().remove(personIndex);
    }

    private ProjectRole findProjectByName(String name, List<Object> projects) {
        for (Object obj : projects) {
            ProjectRole project = (ProjectRole) obj;
            if (project.getProject().getProjectName().equals(name))
                return project;
        }
        return null;
    }

    private void insertar(String roleName, List<String> workersName, List<RoleWorker> roleWorkers, List<PersonEntity> workers, List<RoleEntity> roles) {
        RoleEntity role = findRoleByName(roleName, roles);
        List<PersonEntity> linkedList = new LinkedList<>();
        workersName.forEach(w -> linkedList.add(findWorkerByName(w, workers)));

        RoleWorker roleWorker = new RoleWorker();
        roleWorker.setFixedWorkers(new LinkedList<>());
        roleWorker.setNeededWorkers((long) workersName.size());
        roleWorker.setRole(role);
        roleWorker.setWorkers(linkedList);
        roleWorkers.add(roleWorker);
    }

    private PersonEntity findWorkerByName(String workerName, List<PersonEntity> workers) {
        return workers.stream().filter(p -> p.getPersonName().equals(workerName)).findFirst().orElse(null);
    }

    private RoleEntity findRoleByName(String roleName, List<RoleEntity> roles) {
        return roles.stream().filter(r -> r.getRoleName().equals(roleName)).findFirst().orElse(null);
    }

    private PersonEntity getRandomWorker(List<PersonEntity> workers) {
        Random random = new SecureRandom();
        PersonEntity nextRandomWorker = null;
        if(!workers.isEmpty()){
            nextRandomWorker = workers.get(random.nextInt(workers.size()));
        }
        return nextRandomWorker;
    }

    private RoleWorker getRandomRoleWorker(List<RoleWorker> roleWorkers) {
        Random random = new SecureRandom();
        return roleWorkers.get(random.nextInt(roleWorkers.size()));
    }

    private List<PersonEntity> getAvailableWorkers(List<PersonEntity> allWorkers, List<PersonEntity> assignedWorkers) {
        List<PersonEntity> copyAllWorkers = new LinkedList<>(allWorkers);
        copyAllWorkers.removeAll(assignedWorkers); // me quedo solo con los disponibles
        return copyAllWorkers;
    }

    private List<RoleWorker> getAssignedRolesByWorker(List<RoleWorker> assignedRoles, PersonEntity workerToFind) {
        List<RoleWorker> result = new LinkedList<>();
        assignedRoles.forEach(roleWorker -> roleWorker.getWorkers().forEach(worker -> {
            if (worker.equals(workerToFind)) result.add(roleWorker);
        }));
        return result;
    }

    /**
     * @param allWorkers lista de todos los trabajadores
     * @param cantWorker cant de trabajadores a buscar
     * @return lista de tamaño cantWorker
     */
    private List<PersonEntity> getNInitialWorkers(List<PersonEntity> allWorkers, int cantWorker) {
        List<PersonEntity> disponibles = new LinkedList<>(allWorkers);
        List<PersonEntity> asignados = new LinkedList<>();
        Random random = new SecureRandom();
        while (cantWorker > 0) {
            PersonEntity worker = disponibles.get(random.nextInt());
            asignados.add(worker);
            disponibles.remove(worker);
            cantWorker--;
        }
        return asignados;
    }

    public static ArrayList<PersonEntity> personasSolucion(ArrayList<Object> code) throws IOException, ClassNotFoundException {
        // devuelve un arreglo con las personas que hay en la solución
        ProjectRole rp;
        RoleWorker rw;

        ArrayList<PersonEntity> personas = new ArrayList<>();
        for (Object o : code) {
            rp = ((ProjectRole) o);
            for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                rw = rp.getRoleWorkers().get(j);
                personas.addAll(rw.getWorkers());
            }
        }
        return personas;
    }

    /**
     * Che
     *
     * @param state
     * @return
     */
    public boolean isTeamComplete(ProjectRoleState state) {

        List<Object> projects = state.getCode(); // obtener lista de proyectos -roles

        int i = 0;
        boolean isComplete = true;
        while (i < projects.size() && isComplete) { //para cada projecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            int j = 0;
            while (j < roleWorkers.size() && isComplete) { //para cada rol-persona
                RoleWorker roleWorker = roleWorkers.get(j);

                int peopleInRole = roleWorker.getWorkers().size() + roleWorker.getFixedWorkers().size(); //total de personas asignadas al rol

                if (roleWorker.getNeededWorkers() != 0) {
                    isComplete = false;
                }
                j++;
            }
            i++;
        }
        return isComplete;
    }

    //TRABAJO EN CURSO
    public ArrayList<CompetenceProjectRole> RestrictSearchAreaCompetences(ProjectRoleState initialState, TeamFormationCodification codification) {
        List<PersonEntity> searchArea = codification.getSearchArea();
        List<Object> projects = initialState.getCode();
        ArrayList<CompetenceProjectRole> restProjects = new ArrayList<>(projects.size());

        for (Object project : projects) {
            ProjectRole projectRole = (ProjectRole) project;
            CompetenceProjectRole newProjectRole = new CompetenceProjectRole(projectRole.getProject(), null);

            List<CycleEntity> cycleList = projectRole.getProject().getCycleList();
            List<ProjectRolesEntity> projRoles = cycleList.getLast().getProjectStructure().getProjectRolesList(); //Listado de roles
            ArrayList<CompetenceRoleWorker> restRoleWorkers = new ArrayList<>(projRoles.size());

            for (ProjectRolesEntity projRole : projRoles) {

                ArrayList<CompetentWorker> competentWorkers = new ArrayList<>();
                CompetenceRoleWorker newRoleWorker = new CompetenceRoleWorker(projRole.getRole(), null);

                List<ProjectTechCompetenceEntity> projTechComps = projRole.getProjectTechCompetenceList(); //Listado de competencias tecnicas de un rol en un proyecto
                List<RoleCompetitionEntity> roleCompetitions = projRole.getRole().getRoleCompetitionList(); //Listado de competencias génericas de un rol

                for (PersonEntity worker : searchArea) {
                    CompetentWorker competentWorker = new CompetentWorker();//AQUI
                    List<CompetenceValueEntity> competenceValues = worker.getCompetenceValueList();

                    competentWorker.setWorker(worker);
                    long evaluation = competenceEvaluation(projTechComps, roleCompetitions, competenceValues);

                    if (evaluation != -1) {
                        competentWorker.setEvaluation(evaluation);
                        competentWorkers.add(competentWorker);
                    }
                }

                Comparator<CompetentWorker> c = new Comparator<CompetentWorker>() {
                    @Override
                    public int compare(CompetentWorker o1, CompetentWorker o2) {

                        return Long.compare(o2.getEvaluation(), o1.getEvaluation());

                    }
                };
                competentWorkers.sort(c);
                newRoleWorker.setWorkers(competentWorkers);
                restRoleWorkers.add(newRoleWorker);
            }
            newProjectRole.setRoleWorkers(restRoleWorkers);
            restProjects.add(newProjectRole);
        }
        return restProjects;
    }

    public ArrayList<BelbinCategoryRole> RestrictSearchAreaBelbinRoles(ProjectRoleState initialState, TeamFormationCodification codification) {
        List<PersonEntity> searchArea = codification.getSearchArea();
        ArrayList<BelbinCategoryRole> belbinRoles = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            belbinRoles.add(new BelbinCategoryRole(new ArrayList<>()));
        }

        for (PersonEntity worker : searchArea) {
            PersonTestEntity workerTest = worker.getPersonTest();

            if (workerTest != null) {
                //              int code = 0;
                //action role
                if ((workerTest.getIM() != 'I' && workerTest.getIM() != 'E') || (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') || (workerTest.getIF() != 'I' && workerTest.getIF() != 'E')) {
                    belbinRoles.get(1).getCategoryWorkers().add(worker); // code += 010;
                }

                //mental role
                if ((workerTest.getCE() != 'I' && workerTest.getCE() != 'E') || (workerTest.getME() != 'I' && workerTest.getME() != 'E') || (workerTest.getES() != 'I' && workerTest.getES() != 'E')) {
                    belbinRoles.get(2).getCategoryWorkers().add(worker); // code += 001;
                }

                //social role
                if ((workerTest.getCO() != 'I' && workerTest.getCO() != 'E') || (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') || (workerTest.getIR() != 'I' && workerTest.getIR() != 'E')) {
                    belbinRoles.getFirst().getCategoryWorkers().add(worker); // code += 100;
                }
            }
        }
        return belbinRoles;
    }

    public long competenceEvaluation(List<ProjectTechCompetenceEntity> projTechComps, List<RoleCompetitionEntity> roleCompetitions, List<CompetenceValueEntity> competenceValues) {
        long evaluation = 0;
        boolean found = false;
        boolean skip = false;

        if (!projTechComps.isEmpty()) {
            for (int l = 0; l < projTechComps.size() && !skip; l++) { //Ciclo competencias técnicas
                int m = 0;
                ProjectTechCompetenceEntity techCompetence = projTechComps.get(l);
                found = false;
                while (!found && m < competenceValues.size()) {
                    if (competenceValues.get(m).getCompetence().getId().equals(techCompetence.getCompetence().getId())) {
                        if (competenceValues.get(m).getLevel().getLevels() < techCompetence.getLevel().getLevels()) {
                            skip = true;
                        } else {
                            evaluation += competenceValues.get(m).getLevel().getLevels() * techCompetence.getCompetenceImportance().getLevels();
                        }
                        found = true;
                    }
                    m++;
                }
                if (found == false) {
                    skip = true;
                }
            }
        }

        if (!skip && !roleCompetitions.isEmpty()) {
            for (int n = 0; n < roleCompetitions.size() && !skip; n++) { //Ciclo competencias genéricas
                int o = 0;
                RoleCompetitionEntity roleCompetence = roleCompetitions.get(n);
                found = false;
                while (!found && o < competenceValues.size()) {
                    if (competenceValues.get(o).getCompetence().getId().equals(roleCompetence.getCompetence().getId())) {
                        if (competenceValues.get(o).getLevel().getLevels() < roleCompetence.getLevel().getLevels()) {
                            skip = true;
                        } else {
                            evaluation += competenceValues.get(o).getLevel().getLevels() * roleCompetence.getCompetenceImportance().getLevels();
                        }
                        found = true;
                    }
                    o++;
                }
                if (!found) {
                    skip = true;
                }
            }
        }
        return skip ? -1 : evaluation;
    }

    public String checkBelbinCategories(ProjectRole checkProject) {
        int workerBelbinCategories = 0;

        List<PersonEntity> projectWorkers = ObjetiveFunctionUtil.ProjectWorkers(checkProject);

        for (PersonEntity projectWorker : projectWorkers) {
            PersonTestEntity workerTest = projectWorker.getPersonTest(); //obtener caracteristicas psicologicas

            if (workerTest != null) {
                //action role
                if ((workerTest.getIM() != 'I' && workerTest.getIM() != 'E') || (workerTest.getIS() != 'I' && workerTest.getIS() != 'E') || (workerTest.getIF() != 'I' && workerTest.getIF() != 'E')) {
                    workerBelbinCategories += 10;
                }
                //mental role
                if ((workerTest.getCE() != 'I' && workerTest.getCE() != 'E') || (workerTest.getME() != 'I' && workerTest.getME() != 'E') || (workerTest.getES() != 'I' && workerTest.getES() != 'E')) {
                    workerBelbinCategories += 1;
                }
                //social role
                if ((workerTest.getCO() != 'I' && workerTest.getCO() != 'E') || (workerTest.getCH() != 'I' && workerTest.getCH() != 'E') || (workerTest.getIR() != 'I' && workerTest.getIR() != 'E')) {
                    workerBelbinCategories += 100;
                }
            }
        }
        String codeWorkerBelbinCategories = String.valueOf(workerBelbinCategories);
        while (codeWorkerBelbinCategories.length() < 3) {
            codeWorkerBelbinCategories = "0".concat(codeWorkerBelbinCategories);
        }
        return codeWorkerBelbinCategories;
    }

    /**
     * @param allRoles                 lista que contiene todos los roles del proyecto
     * @param assignedRoles            lista que contiene los roles que fueron asignados
     * @param roleWorkerToBeCompatible rol del cual se quieren sacar las compatibilidades
     * @return devuelve un <code>RoleWorker</code> aleatorio que sea compatible con <code>roleWorkerToBeCompatible</code> y este disponible
     */
    private RoleWorker getRandomCompatibleRole(List<RoleWorker> allRoles, RoleWorker roleWorkerToBeCompatible, List<RoleWorker> assignedRoles) {
        List<RoleWorker> allRolesCopy = new LinkedList<>(allRoles);
        RoleEntity roleToBeCompatible = roleWorkerToBeCompatible.getRole(); // rol para buscar los roles compatibles

        // roles incompatibles con el rol seleccionado
        List<RoleEntity> incompatibleRoles = roleToBeCompatible.getIncompatibleRoles();

        // quito de todos los roles del proyecto aquellos que son incompatibles con el rol seleccionado
        List<RoleWorker> toDelte = new LinkedList<>(); // lista de roles a borrar
        /*se anade como rol a borrar xq si no, al rol no ser incompatible con
        él mismo, lo añade como posibe rol compatible, generando una */
        toDelte.add(roleWorkerToBeCompatible);
        incompatibleRoles.forEach(roleInc -> allRolesCopy.forEach(roleWorker -> {
            if (roleInc.equals(roleWorker.getRole())) {
                toDelte.add(roleWorker);
            }
        }));
        allRolesCopy.removeAll(toDelte);

        // de esos compatibles me tengo que quedar con aquellos que estan disponibles
        return getRandomAvailableRole(allRolesCopy, assignedRoles);
    }

    private boolean existAvailableRoles(List<RoleWorker> allProjectRoles, List<RoleWorker> assignedRoleWorkers) {
        /*si es distinto de null es porque existen*/
        return getRandomAvailableRole(allProjectRoles, assignedRoleWorkers) != null;
    }

    /**
     * busca un rol aleatorio disponible distinto del lider
     *
     * @param allProjectRoles     lista que contiene todos los roles del proyecto
     * @param assignedRoleWorkers lista que contiene los roles asignados
     * @return rol aleatorio disponible distinto del Líder o null si no encuentra ninguno
     */
    private RoleWorker getRandomAvailableRole(List<RoleWorker> allProjectRoles, List<RoleWorker> assignedRoleWorkers) {
        Random random = new SecureRandom();
        // copia de la lista original para no afectar a la original
        List<RoleWorker> notAssignedRoles = new LinkedList<>(allProjectRoles);
        // quito los asignados para quedarme solamente con los disponibles
        notAssignedRoles.removeAll(assignedRoleWorkers);
        // quito el rol lider xq esto lo asigno al final
        notAssignedRoles.stream().filter(roleWorker -> roleWorker.getRole().isBoss()).findFirst().ifPresent(notAssignedRoles::remove);
        // devuelvo uno aleatorio
        try {
            return notAssignedRoles.get(random.nextInt(notAssignedRoles.size()));
        } catch (Exception e) {
            return null; // si no hay es porque ya se asignaron todos
        }
    }

    /**
     * @param allWorkers      lista con todos los workers disponibles para la solución
     * @param assignedWorkers lista con todos los trabajadores asignados a roles
     * @return el worker seleccionado o null si no existe alguno
     */
    private PersonEntity getRandomNotAssignedWorker(List<PersonEntity> allWorkers, List<PersonEntity> assignedWorkers) {
        Random random = new SecureRandom();
        List<PersonEntity> notAssignedWorkers = new LinkedList<>(allWorkers);
        notAssignedWorkers.removeAll(assignedWorkers);
        try {
            return notAssignedWorkers.get(random.nextInt(notAssignedWorkers.size()));
        } catch (Exception e) {
            return null;
        }
    }

    public static RoleEntity rolAsignadoAPersona(ArrayList<Object> code, PersonEntity persona) throws IOException, ClassNotFoundException {
        //dado una persona y una solución devuelve el rol al que está asignado dentro de la solución
        ProjectRole rp;
        RoleWorker rw;
        RoleEntity rol = null;
        for (Object o : code) {
            rp = ((ProjectRole) o);
            for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                rw = rp.getRoleWorkers().get(j);
                for (int k = 0; k < rw.getWorkers().size(); k++)
                    if (rw.getWorkers().get(k).getCard().equals(persona.getCard())) {
                        rol = rw.getRole();
                        break;
                    }
            }
        }
        return rol;
    }

    public static PersonEntity personaConMayorExperienciaParaRolSolucion(Long idRol, ArrayList<PersonEntity> personas) throws IOException, ClassNotFoundException {
        // Este método busca la persona de las disponibles que mayor preferencia presenta
        //para el rol que se va a asignar en la nueva solución

        RoleExperienceEntity experiencia = new RoleExperienceEntity();
        PersonEntity per = personas.getFirst();
        float mayor = 0;
        for (int i = 1; i < personas.size(); i++) {
            PersonEntity per2 = personas.get(i);

            if (null != per2.getRoleExperienceList()) {
                if (!per2.getRoleExperienceList().isEmpty()) {
                    experiencia = per2.getRoleExperience(idRol);
                }
            }

            //experiencia = (RoleExperience) personas.get(i).getRoleExperience(idRol);
            if ((experiencia != null) && (experiencia.getId() != null)) {
                if (experiencia.getIndexes() > mayor || experiencia.getIndexes() == mayor) {
                    mayor = experiencia.getIndexes();
                    per = personas.get(i);
                }

            }
        }
        return per;
    }

    public static PersonEntity personaMayorIdoneidadParaSolucion(ArrayList<PersonEntity> persProy, Long idRol, ArrayList<PersonEntity> personas) throws IOException, ClassNotFoundException {
        // la idoneidad para la solucion es determinada por la suma de la experiencia por el rol a desempeñar y las incompatibilidades
        RoleExperienceEntity experiencia1 = new RoleExperienceEntity(), experiencia2 = new RoleExperienceEntity();
        PersonEntity per = personas.getFirst();

        double inc, exp = 0, exp2 = 0, incomp, mayor = 0;

        for (PersonEntity persona : personas) {
            if (!persona.getRoleExperienceList().isEmpty()) {
                experiencia1 = persona.getRoleExperience(idRol);
            }
            if (experiencia1 != null || experiencia1.getId() != null)
                exp = experiencia1.getIndexes();
            incomp = incompatibilidadesDobles(persona, persProy);
            inc = 1 - incomp / (persProy.size() * 2);// valor de comp promedio
            if ((exp + inc) > mayor) {
                mayor = (exp + inc);
                per = persona;
            } else if ((exp + inc) == mayor) {
                if (!per.getRoleExperienceList().isEmpty()) {
                    experiencia2 = per.getRoleExperience(idRol);
                }
                if (experiencia2 != null || experiencia2.getId() == null)
                    exp2 = experiencia2.getIndexes();
                if (exp > exp2)
                    per = persona;
            }
        }
        return per;
    }

    public static double incompatibilidadesDobles(PersonEntity per, ArrayList<PersonEntity> persProy) throws IOException, ClassNotFoundException {

        double inc1 = 0, inc2 = 0;
        if (!per.getPersonConflictList().isEmpty()) {
            List<PersonConflictEntity> perConflicts = per.getPersonConflictList();
            for (PersonEntity worker : persProy) {
                for (int j = 0; j < worker.getPersonConflictList().size(); j++) {
                    //incompatibilidad de las personas que ya han sido asignadas al proyecto con la nueva persona
                    //que se pretende asignar
                    String id = worker.getPersonConflictList().get(j).getPersonConflict().getCard();
                    if (id.equals(per.getCard())) {
                        inc1 += worker.getPersonConflictList().get(j).getIndex().getWeight();
                    }
                    // incompatibilidad de la persona q se pretende asignar con las que ya han sido asignadas
                    for (PersonConflictEntity perConflict : perConflicts) {
                        id = perConflict.getPersonConflict().getCard();
                        String id2 = worker.getPersonConflictList().get(j).getPersonConflict().getCard();
                        if (id.equals(id2)) {
                            inc2 += perConflict.getIndex().getWeight();
                        }
                    }

                }
            }
        }
        return inc1 + inc2;
    }

    public List<RoleEntity> obtenerRolesState(State prState) {

        List<RoleEntity> roleState = new ArrayList<>();
        for (int i = 0; i < prState.getCode().size(); i++) {
            ProjectRole rp = ((ProjectRole) prState.getCode().get(i));
            for (int j = 0; j < rp.getRoleWorkers().size(); j++) {
                RoleWorker rw = rp.getRoleWorkers().get(j);
                int k = 0;
                boolean found = false;
                while (k < roleState.size() && !found) {
                    if (roleState.get(k).getId().equals(rw.getRole().getId())) {
                        found = true;
                    }
                    k++;
                }
                if (!found) {
                    roleState.add(rw.getRole());
                }
            }
        }
        return roleState;
    }

    public void llenarExperiencias(State prState) {
        List<RoleEntity> roleState = obtenerRolesState(prState);
        TeamFormationCodification codification = (TeamFormationCodification) Strategy.getStrategy().getProblem().getCodification();
        List<PersonEntity> workers = codification.getSearchArea();
        Random random = new Random();
        for (PersonEntity worker : workers) { // para cada worker
            List<RoleExperienceEntity> exp = new ArrayList<>();
            for (RoleEntity role : roleState) {
                RoleExperienceEntity experiencia = new RoleExperienceEntity();
                experiencia.setRole(role);
                experiencia.setIndexes(random.nextFloat());
                experiencia.setPerson(worker);
                exp.add(experiencia);
            }
            worker.setRoleExperienceList(exp);
            codification.getSearchArea().set(workers.indexOf(worker), worker);
        }
    }

    private boolean isInvalidState(List<State> toReturn, int count, TeamFormationCodification codification) {
        boolean invalidState = true;
        if (codification.validState(toReturn.getFirst())) { //Esto es una NOTA informativa
            invalidState = false;
            //Va en la condicional
            //Integer.parseInt(ResourceBundle.getBundle("/algorithmConf").getString("posibleValidateNumber"))
        } else if (count < AlgorithmConfig.getInt("posibleValidateNumber", 20)) {
            toReturn.clear();
        }//Va en la condicional
        //Integer.parseInt(ResourceBundle.getBundle("/algorithmConf").getString("posibleValidateNumber"))
        if (count >= AlgorithmConfig.getInt("posibleValidateNumber", 20)) {
            int iterations = AlgorithmConfig.getInt("iterations", 100);
            Strategy.getStrategy().setCountCurrent(iterations);
        }
        return invalidState;
    }

    private boolean repareState(List<State> toReturn, int cantIntentos, int count, TeamFormationCodification codification) {
        boolean stop = codification.validState(toReturn.getFirst());
        int i = 0;
        while (!stop && i < cantIntentos) {
            try {
                i++;
                codification.repareState(toReturn.getFirst());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(TeamFormationOperator.class.getName()).log(Level.SEVERE, null, ex);
            }
            stop = codification.validState(toReturn.getFirst());
        }
        return isInvalidState(toReturn, count, codification);
    }
}

