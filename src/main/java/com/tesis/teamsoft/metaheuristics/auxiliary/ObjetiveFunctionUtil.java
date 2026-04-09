package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.metaheuristics.objetives.*;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.extension.TypeSolutionMethod;

import java.util.ArrayList;
import java.util.List;
import problem.definition.State;

/**
 * @author G1lb3rt
 */
public class ObjetiveFunctionUtil {

    /**
     * Balancear la funcion objetivo
     *
     * @param totalSum
     * @return
     */
    public static double balance(List<Double> totalSum) {
        double balance = 0;
        double diference;
        double average = average(totalSum);

        for (Double item : totalSum) {
            diference = average - item;
            balance += diference > 0 ? diference : -diference;
        }
        return balance;
    }

    /**
     * Calcular el promedio de los valores de la funcion objetivo para cada
     * proyecto
     *
     * @param projectSum
     * @return
     */
    public static double average(List<Double> projectSum) {
        double average = -1;
        double total = 0;
        int count = 0;

        if (projectSum != null) {
            for (Double item : projectSum) {
                total += item;
                count++;
            }

            if (!projectSum.isEmpty()) {
                average = total / count;
            }
        }

        return average;
    }

    /**
     * Chequear si una persona posee un nivel minimo en una competencia dada una
     * lista de competences value
     *
     * @param personCompetences
     * @param comp
     * @param minValue
     * @return
     */
    public static boolean validatePersonCompetences(List<CompetenceValueEntity> personCompetences, CompetenceEntity comp, Long minValue) {

        boolean meet = false;

        int i = 0;
        boolean found = false;
        while (i < personCompetences.size() && !found) { //comprobar que la persona posee la competencia
            if (personCompetences.get(i).getCompetence().getId().equals(comp.getId())) { //si es la competencia que se mide
                found = true;
                if (personCompetences.get(i).getLevel().getLevels() >= minValue) { // si posee el nivel minimo
                    meet = true;
                }
            }
            i++;
        }

        return meet;
    }

    /**
     * Obtener el nivel de una persona en una competencia dada
     *
     * @param personCompetences
     * @param comp
     * @return
     */
    public static long personCompetenceLevel(List<CompetenceValueEntity> personCompetences, CompetenceEntity comp) {

        long level = 0;

        int i = 0;
        boolean found = false;
        while (i < personCompetences.size() && !found) { //buscar la competencia
            if (personCompetences.get(i).getCompetence().getId().equals(comp.getId())) { //si es la competencia deseada
                found = true;
                level = personCompetences.get(i).getLevel().getLevels();
            }
            i++;
        }

        return level;
    }

    /**
     * Calcular experiencia en el desempeño del rol.
     *
     * @param roleEvaluation
     * @param desireRole
     * @return
     */
    public static Long evaluationByRole(List<RolePersonEvalEntity> roleEvaluation, RoleEntity desireRole) {
        long sum = 0;

        for (RolePersonEvalEntity item : roleEvaluation) {
            if (desireRole.getId().equals(item.getRoles().getId())) {
                sum += (long) item.getRoleEvaluation().getLevels();
            }
        }
        return 1 - (1 / (1 + sum));
    }

    /**
     * Cantidad de veces que se ha desempeñado un rol.
     *
     * @param roleEvaluation
     * @param desireRole
     * @return
     */
    public static Long roleEvaluationCount(List<RolePersonEvalEntity> roleEvaluation, RoleEntity desireRole) {
        long sum = 0;

        for (RolePersonEvalEntity item : roleEvaluation) {
            if (desireRole.getId().equals(item.getRoles().getId())) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Obtener funciones objetivo
     *
     * @param parameters
     * @return
     */
    public static List<ObjetiveFunction> getObjectiveFunctions(TeamFormationParameters parameters) {

        List<ObjetiveFunction> objectiveFunctions = new ArrayList<>();

        if (parameters != null) {
            if (parameters.isMaxCompetences()) { //maximizar competencias
                MaximizeCompetency maxCompetence = new MaximizeCompetency();
                maxCompetence.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxCompetence.setWeight(parameters.getMaxCompetencesWeight());
                    maxCompetence.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxCompetence);
            }
            if (parameters.isMaxInterests()) {  //maximizar intereces por rol
                MaximizeInterests maxInterest = new MaximizeInterests();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxInterest.setWeight(parameters.getMaxInterestsWeight());
                    maxInterest.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxInterest);
            }
            if (parameters.isMaxProjectInterests()) {  //maximizar intereces por proyecto rafiki
                MaximizeProjectInterests maxProjectInterest = new MaximizeProjectInterests();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxProjectInterest.setWeight(parameters.getMaxProjectInterestsWeight());
                    maxProjectInterest.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxProjectInterest);
            }
            if (parameters.isMaxBelbinRoles()) {  //maximizar roles de belbin
                MaximizeBelbinRoles maxBelbinRoles = new MaximizeBelbinRoles();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxBelbinRoles.setWeight(parameters.getMaxBelbinWeight());
                    maxBelbinRoles.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxBelbinRoles);
            }
            if (parameters.isMaxMbtiTypes()) {  //maximizar tipos MBTI
                MaximizeMbtiTypes maximizeMbtiTypes = new MaximizeMbtiTypes();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maximizeMbtiTypes.setWeight(parameters.getMaxMbtiTypesWeight());
                    maximizeMbtiTypes.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maximizeMbtiTypes);
            }

            if (parameters.isMinIncomp()) {  //minimizar incompatibilidades
                MinimizeIncompatibilities minIncompatibilities = new MinimizeIncompatibilities(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minIncompatibilities.setWeight(parameters.getMinIncompWeight());
                    minIncompatibilities.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minIncompatibilities);
            }

            if (parameters.isMinCostDistance()) {  //minimizar costo por distancia
                MinimizeCostDistance minCostDistance = new MinimizeCostDistance(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minCostDistance.setWeight(parameters.getMinCostDistanceWeight());
                    minCostDistance.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minCostDistance);
            }

            if (parameters.isTakeWorkLoad()) {  //balancear carga de trabajo personal
                MinimizeWorkload minimizeWorkload = new MinimizeWorkload();
                minimizeWorkload.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minimizeWorkload.setWeight(parameters.getWorkLoadWeight());
                    minimizeWorkload.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minimizeWorkload);
            }
            if (parameters.isMaxMultiroleTeamMembers()) { //maximizar competencias
                MaximizeMultiroleTeamMembers maxMultiroleTeamMembers = new MaximizeMultiroleTeamMembers();
                maxMultiroleTeamMembers.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxMultiroleTeamMembers.setWeight(parameters.getMaxMultiroleTeamMembersWeight());
                    maxMultiroleTeamMembers.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxMultiroleTeamMembers);
            }
            if (parameters.isMaxSex()) { //Heterogeneus Sex
                MaximizeSexFactor maximizeSexFactor = new MaximizeSexFactor();
                maximizeSexFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maximizeSexFactor.setWeight(parameters.getMaxSexWeight());
                    maximizeSexFactor.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maximizeSexFactor);
            }

            if (parameters.isMinSex()) { //Homogeneus Sexo
                MinimizeSexFactor minimizeSexFactor = new MinimizeSexFactor();
                minimizeSexFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minimizeSexFactor.setWeight(parameters.getMinSexWeight());
                    minimizeSexFactor.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minimizeSexFactor);
            }

            if (parameters.isHeterogeneousTeams()) { //formar equipos heterogeneos
                MaximizeNacionalityFactor maximizeNacionalityFactor = new MaximizeNacionalityFactor();
                maximizeNacionalityFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maximizeNacionalityFactor.setWeight(parameters.getHeterogeneousTeamsWeight());
                    maximizeNacionalityFactor.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maximizeNacionalityFactor);
            }
            if (parameters.isHomogeneousTeams()) { //formar equipos heterogeneos
                MinimizeNacionalityFactor minimizeNacionalityFactor = new MinimizeNacionalityFactor();
                minimizeNacionalityFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minimizeNacionalityFactor.setWeight(parameters.getHomogeneousTeamsWeight());
                    minimizeNacionalityFactor.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minimizeNacionalityFactor);
            }

            //PARA BALANCEAR ENTRE EQUIPOS
            if (parameters.isBalanceCompetences()) { //balancear las competencias
                BalanceMaximizeCompetency maxCompetency = new BalanceMaximizeCompetency();
                maxCompetency.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxCompetency.setWeight(parameters.getBalanceCompetenceWeight());
                    maxCompetency.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(maxCompetency);
            }
            if (parameters.isBalanceInterests()) { //balancear los intereces por rol
                BalanceMaximizeInterests maxInterest = new BalanceMaximizeInterests();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxInterest.setWeight(parameters.getBalanceInterestWeight());
                    maxInterest.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(maxInterest);
            }
            if (parameters.isBalanceProjectInterests()) { //balancear los intereses por proyecto
                BalanceMaximizeProjectInterests maxProjectInterest = new BalanceMaximizeProjectInterests();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxProjectInterest.setWeight(parameters.getBalanceProjectInterestWeight());
                    maxProjectInterest.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxProjectInterest);
            }
            if (parameters.isBalanceMbtiTypes()) { //balancear los tipos mbti
                BalanceMaximizeMbtiTypes maxMbtiTypes = new BalanceMaximizeMbtiTypes();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxMbtiTypes.setWeight(parameters.getBalanceMbtiTypesWeight());
                    maxMbtiTypes.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxMbtiTypes);
            }
            if (parameters.isBalanceBelbinRoles()) { //balancear los roles de belbin
                BalanceMaximizeBelbinRoles maxBelbinRoles = new BalanceMaximizeBelbinRoles();
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxBelbinRoles.setWeight(parameters.getBalanceBelbinWeight());
                    maxBelbinRoles.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(maxBelbinRoles);
            }
            if (parameters.isBalanceCostDistance()) { //balancear costo por distancia
                BalanceMinimizeCostDistance minCostDistance = new BalanceMinimizeCostDistance(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minCostDistance.setWeight(parameters.getBalanceCostDistanceWeight());
                    minCostDistance.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minCostDistance);
            }
            if (parameters.isBalanceSynergy()) { //balancear costo por distancia
                BalanceMinimizeIncompatibilities minIncompatibilities = new BalanceMinimizeIncompatibilities(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minIncompatibilities.setWeight(parameters.getBalanceSynergyWeight());
                    minIncompatibilities.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minIncompatibilities);
            }

            if (parameters.isBalancePersonWorkload()) {  //balancear carga de trabajo personal
                BalanceMinimizeWorkload minWorkload = new BalanceMinimizeWorkload();
                minWorkload.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minWorkload.setWeight(parameters.getBalanceWorkLoadWeight());
                    minWorkload.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minWorkload);
            }
            if (parameters.isBalanceMultiroleTeamMembers()) { //balancear personas multirol en el equipo
                BalanceMaximizeMultiroleTeamMembers maxMultiroleTM = new BalanceMaximizeMultiroleTeamMembers();
                maxMultiroleTM.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxMultiroleTM.setWeight(parameters.getBalanceMultiroleTeamMembersWeight());
                    maxMultiroleTM.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(maxMultiroleTM);

            }
            if (parameters.isBalanceMaximizeSexFactor()) { //balancear personas respecto al sexo
                BalanceMaximizeSexFactor balanceMaximizeSexFactorTM = new BalanceMaximizeSexFactor();
                balanceMaximizeSexFactorTM.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMaximizeSexFactorTM.setWeight(parameters.getBalanceMaximizeSexFactorWeight());
                    balanceMaximizeSexFactorTM.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(balanceMaximizeSexFactorTM);

            }
            if (parameters.isBalanceMinimizeSexFactor()) { //balancear personas respecto al sexo
                BalanceMinimizeSexFactor balanceMinimizeSexFactorTM = new BalanceMinimizeSexFactor();
                balanceMinimizeSexFactorTM.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMinimizeSexFactorTM.setWeight(parameters.getBalanceMinimizeSexFactorWeight());
                    balanceMinimizeSexFactorTM.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMinimizeSexFactorTM);

            }
            if (parameters.isBalanceHeterogeneousTeams()) { //balancear equipo heterogeneo
                BalanceMaximizeNacionalityFactor balanceMaximizeNacionalityFactor = new BalanceMaximizeNacionalityFactor();
                balanceMaximizeNacionalityFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMaximizeNacionalityFactor.setWeight(parameters.getBalanceHeterogeneousTeamsNacionalityFactorWeight());
                    balanceMaximizeNacionalityFactor.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(balanceMaximizeNacionalityFactor);

            }
            if (parameters.isBalanceHomogeneousTeams()) { //balancear equipo homogéneo
                BalanceMinimizeNacionalityFactor balanceMinimizeNacionalityFactor = new BalanceMinimizeNacionalityFactor();
                balanceMinimizeNacionalityFactor.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMinimizeNacionalityFactor.setWeight(parameters.getBalanceHomogeneousTeamsNacionalityFactorWeight());
                    balanceMinimizeNacionalityFactor.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMinimizeNacionalityFactor);

            }

            // Para religión: maximizar heterogeneidad
            if (parameters.isMaxReligion()) {
                MaximizeReligionFactor maxReligion = new MaximizeReligionFactor();
                maxReligion.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxReligion.setWeight(parameters.getMaxReligionWeight());
                    maxReligion.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxReligion);
            }

            // Para religión: minimizar homogeneidad
            if (parameters.isMinReligion()) {
                MinimizeReligionFactor minReligion = new MinimizeReligionFactor();
                minReligion.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minReligion.setWeight(parameters.getMinReligionWeight());
                    minReligion.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minReligion);
            }

            // Para religión: balancear heterogeneidad
            if (parameters.isBalanceMaximizeReligion()) {
                BalanceMaximizeReligionFactor balanceMaxReligion = new BalanceMaximizeReligionFactor();
                balanceMaxReligion.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMaxReligion.setWeight(parameters.getBalanceMaximizeReligionWeight());
                    balanceMaxReligion.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMaxReligion);
            }

            // Para religión: balancear homogeneidad
            if (parameters.isBalanceMinimizeReligion()) {
                BalanceMinimizeReligionFactor balanceMinReligion = new BalanceMinimizeReligionFactor();
                balanceMinReligion.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMinReligion.setWeight(parameters.getBalanceMinimizeReligionWeight());
                    balanceMinReligion.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMinReligion);
            }

            // Para edad: maximizar heterogeneidad
            if (parameters.isMaxAgeHeterogeneity()) {
                MaximizeAgeFactor maxAgeHeterogeneity = new MaximizeAgeFactor();
                maxAgeHeterogeneity.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    maxAgeHeterogeneity.setWeight(parameters.getMaxAgeHeterogeneityWeight());
                    maxAgeHeterogeneity.setTypeProblem(Problem.ProblemType.Maximizar);
                }
                objectiveFunctions.add(maxAgeHeterogeneity);
            }

            // Para edad: minimizar homogeneidad
            if (parameters.isMinAgeHomogeneity()) {
                MinimizeAgeFactor minAgeHomogeneity = new MinimizeAgeFactor();
                minAgeHomogeneity.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    minAgeHomogeneity.setWeight(parameters.getMinAgeHomogeneityWeight());
                    minAgeHomogeneity.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(minAgeHomogeneity);
            }

            // Para edad: balancear heterogeneidad
            if (parameters.isBalanceMaximizeAgeHeterogeneity()) {
                BalanceMaximizeAgeFactor balanceMaxAgeHeterogeneity = new BalanceMaximizeAgeFactor();
                balanceMaxAgeHeterogeneity.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMaxAgeHeterogeneity.setWeight(parameters.getBalanceMaximizeAgeHeterogeneityWeight());
                    balanceMaxAgeHeterogeneity.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMaxAgeHeterogeneity);
            }

            // Para edad: balancear homogeneidad
            if (parameters.isBalanceMinimizeAgeHomogeneity()) {
                BalanceMinimizeAgeFactor balanceMinAgeHomogeneity = new BalanceMinimizeAgeFactor();
                balanceMinAgeHomogeneity.setParameters(parameters);
                if (parameters.getSolutionMethodOptionBoss().equals(TypeSolutionMethod.FactoresPonderados)) {
                    balanceMinAgeHomogeneity.setWeight(parameters.getBalanceMinimizeAgeHomogeneityWeight());
                    balanceMinAgeHomogeneity.setTypeProblem(Problem.ProblemType.Minimizar);
                }
                objectiveFunctions.add(balanceMinAgeHomogeneity);
            }
        }
        //COMMENT

        return objectiveFunctions;
    }

    public static List<PersonEntity> ProjectWorkers(ProjectRole projectRole) {
        List<PersonEntity> projectWorkers = new ArrayList<>(0);
        List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

        int i = 0;
        while (i < roleWorkers.size()) {
            RoleWorker roleWorker = roleWorkers.get(i);
            List<PersonEntity> workers = new ArrayList<>(0);
            workers.addAll(roleWorker.getWorkers());
            workers.addAll(roleWorker.getFixedWorkers());

            int j = 0;
            while (j < workers.size()) {
                PersonEntity worker = workers.get(j);

                int k = 0;
                boolean found = false;
                while (k < projectWorkers.size() && !found) {
                    if (projectWorkers.get(k).getId().equals(worker.getId())) {
                        found = true;
                    }
                    k++;
                }
                if (!found) {
                    projectWorkers.add(worker);
                }
                j++;
            }
            i++;
        }
        return projectWorkers;
    }

    public static int getIdentificator(String className) {
        int id = 0;
        switch (className) {
            case "Competencias": //Maximizar las competencias
                id = 1;
                break;
            case "BalanceCompetencias": //Balancear el índice de competencias
                id = 2;
                break;
            case "Incompatibilidades": //Maximizar la sinergia entre los miembros de los equipos
                id = 3;
                break;
            case "BalanceIncompatibilidades": //Balancear el índice de incompatibilidades
                id = 4;
                break;
            case "CargaTrabajo": //Minimizar la carga de trabajo
                id = 5;
                break;
            case "BalanceCargaTrabajo": //Balancear la carga de trabajo
                id = 6;
                break;
            case "CostoDistancia": //Minimizar el costo de trabajar a distancia
                id = 7;
                break;
            case "BalanceCostoDistancia": //Balancear el costo de trabajar a distancia
                id = 8;
                break;
            case "RolesBelbin": //Maximizar la presencia de los diferentes roles de Belbin
                id = 9;
                break;
            case "BalanceRolesBelbin": //Balancear la presencia de los diferentes roles de Belbin
                id = 10;
                break;
            case "Intereses": //Maximizar los intereses de las personas por desempeñar el rol
                id = 11;
                break;
            case "BalanceIntereses": //Balancear los intereses de las personas por desempeñar el rol
                id = 12;
                break;
            case "ProjectIntereses": //Maximizar el interés de las personas por trabajar en el proyecto
                id = 13;
                break;
            case "BalanceProjectInterests": //Balancear el interés de las personas en trabajar en el (los) proyecto(s)
                id = 14;
                break;
            case "TiposMBTI": //Maximizar la presencia de personas con tipos psicológicos, según el test MBTI
                id = 15;
                break;
            case "BalanceTiposMBTI":
                id = 16; //Balancear la presencia de personas con tipos psicológicos, según test MBTI
                break;
            case "Multirol":
                id = 17; // Maximizar las personas capaces de ocupar mas roles en el equipo
                break;
            case "BalanceMultirol":
                id = 18; // Balancear presencia de personas multirol en el equipo.
                break;
            case "Sexo Heterogéneo":
                id = 19; // Formar equipos  homogeneos tomando en cuenta el factor sexo
                break;
            case "Sexo Homogéneo":
                id = 20; // Formar equipos heterogeneos tomando en cuenta el factor sexo
                break;
            case "Balancear equipos Heterogéneos":
                id = 21; // Balance Max equipos tomando en cuenta el factor sexo
                break;
            case "Balancear equipos Homogéneos":
                id = 22; // Balance Min equipos tomando en cuenta el factor sexo
                break;
            case "Equipo Heterogéneo":
                id = 23; // Formar equipos  heterogeneos tomando en cuenta el factor nacionalidad
                break;
            case "Equipo Homogéneo":
                id = 24; // Formar equipos  homogeneos tomando en cuenta el factor nacionalidad
                break;
            case "Balancear equipos heterogéneos formados":
                id = 25; // Balancear equipo hetereogeneo tomando encuenta el factor nacionalidad
                break;
            case "Balancear equipos homogéneos formados":
                id = 26; // Balancear equipo homogéneos tomando encuenta el factor nacionalidad
                break;
            case "Equipo Heterogéneo Religión":
                id = 27; // Asignar un ID único
                break;
            case "Equipo homogéneos Religión":
                id = 28;
                break;
            case "Balancear equipos heterogéneos religión":
                id = 29;
                break;
            case "Balancear equipos homogéneos religión":
                id = 30;
                break;
            case "Equipo Heterogéneo Edad":
                id = 31; // Asignar un ID único
                break;
            case "Equipo Homogéneo Edad":
                id = 32;
                break;
            case "Balancear equipos heterogéneos edad":
                id = 33;
                break;
            case "Balancear equipos homogéneos edad":
                id = 34;
                break;
        }
        return id;
    }

    public static boolean foundNacionality(NacionalityEntity n, List<NacionalityEntity> list) {
        return list.stream().anyMatch((NacionalityEntity nacionality) -> (nacionality.equals(n)));
    }

    public static boolean foundReligion(ReligionEntity religion, List<ReligionEntity> list) {
        return list.stream().anyMatch((ReligionEntity rel) -> (rel.equals(religion)));
    }

    public static boolean foundAgeGroup(AgeGroupEntity ageGroup, List<AgeGroupEntity> list) {
        return list.stream().anyMatch((ag) -> (ag == ageGroup));
    }

    public static double calculateAgeGroupFactor(State state, TeamFormationParameters parameters) {
        double minValueAgeGroupOrganizationList = 1;
        double minValueAgeGroupTeamList = 1;
        double maxValue;
        double normalizeCoef;
        double sum = 0;
        int amountAgeGroupSearchArea;

        List<Object> projects = state.getCode();  //lista de proyectos - roles
        List<PersonEntity> teamWorkerList = new ArrayList<>();
        List<AgeGroupEntity> ageGroupList = new ArrayList<>();
        List<PersonEntity> organizationWorkerList = parameters.getSearchArea();
        List<Integer> amountTeamList = new ArrayList<>(); //Cant de nac por equipos

        for (PersonEntity w : organizationWorkerList) {
            if (!ObjetiveFunctionUtil.foundAgeGroup(w.getAgeGroup(), ageGroupList)) {
                ageGroupList.add(w.getAgeGroup());
            }
        }

        amountAgeGroupSearchArea = ageGroupList.size();

        for (Object project : projects) {
            ageGroupList.clear();
            teamWorkerList = ObjetiveFunctionUtil.ProjectWorkers((ProjectRole) project);

            for (PersonEntity w : teamWorkerList) {
                if (!ObjetiveFunctionUtil.foundAgeGroup(w.getAgeGroup(), ageGroupList)) {
                    ageGroupList.add(w.getAgeGroup());
                }
            }

            amountTeamList.add(ageGroupList.size());
        }

        if (teamWorkerList.size() >= amountAgeGroupSearchArea) {
            maxValue = amountAgeGroupSearchArea;
        } else {
            maxValue = teamWorkerList.size();
        }

        for (Integer interger : amountTeamList) {
            sum += (interger - minValueAgeGroupTeamList) / (maxValue - minValueAgeGroupOrganizationList);
        }

        normalizeCoef = amountTeamList.isEmpty() ? 0 : sum / amountTeamList.size();

        System.out.println("\n=========================================");
        System.out.println("Max Team: " + amountTeamList.get(0) + " Min Team: 1");
        System.out.println("Max SearchArea: " + maxValue + " Min SearchArea: 1");
        System.out.println("=========================================");

        return normalizeCoef;
    }

    public static double calculateBalanceAgeGroupFactor(State state, TeamFormationParameters parameters) {
        double balanceCoef = 0;
        double sum = 0;
        double sumCoef = 0;
        int amountAgeGroupSearchArea = 0;

        List<Object> projects = state.getCode();  //lista de proyectos - roles
        List<PersonEntity> teamWorkerList = new ArrayList<>();
        List<AgeGroupEntity> ageGroupList = new ArrayList<>();
        List<PersonEntity> organizationWorkerList = parameters.getSearchArea();
        List<Integer> amountTeamList = new ArrayList<>(); //Cant de nac por equipos

        for (PersonEntity w : organizationWorkerList) {
            if (!ObjetiveFunctionUtil.foundAgeGroup(w.getAgeGroup(), ageGroupList)) {
                ageGroupList.add(w.getAgeGroup());
            }
        }

        amountAgeGroupSearchArea = (ageGroupList.isEmpty()) ? 1 : ageGroupList.size();

        for (Object project : projects) {
            ageGroupList.clear();
            teamWorkerList = ObjetiveFunctionUtil.ProjectWorkers((ProjectRole) project);

            for (PersonEntity w : teamWorkerList) {
                if (!ObjetiveFunctionUtil.foundAgeGroup(w.getAgeGroup(), ageGroupList)) {
                    ageGroupList.add(w.getAgeGroup());
                }
            }
            amountTeamList.add(ageGroupList.size());
            sum += ageGroupList.size();
        }
        
        if (!projects.isEmpty()) {                        
            double average = (double) sum / projects.size();

            for(Integer integer: amountTeamList){
                sumCoef += Math.abs(average - integer);
            }                        

            balanceCoef = sumCoef / (projects.size() * amountAgeGroupSearchArea);
        }
        
        return balanceCoef;

    }

    public static double calculateMean(List<Integer> ages) {
        int sum = 0;
        for (int age : ages) {
            sum += age;
        }
        return (double) sum / ages.size();
    }

    public static double calculateVariance(List<Integer> ages, double mean) {
        double sumDiffSq = 0.0;
        for (int age : ages) {
            double diff = age - mean;
            sumDiffSq += diff * diff;
        }
        return sumDiffSq / ages.size();
    }

    public static double calculateMeanDouble(List<Double> values) {
        double sum = 0.0;
        for (double val : values) {
            sum += val;
        }
        return sum / values.size();
    }

    public static double balance(double difSumProject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
