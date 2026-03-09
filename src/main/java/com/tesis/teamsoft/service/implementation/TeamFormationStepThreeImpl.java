package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.auxiliar.Status;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.ITeamFormationStepThreeService;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import local_search.complement.StopExecute;
import local_search.complement.TabuSolutions;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.*;
import com.tesis.teamsoft.pojos.*;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.metaheuristics.operator.TeamBuilder;
import com.tesis.teamsoft.metaheuristics.operator.TeamFormationOperator;
import com.tesis.teamsoft.metaheuristics.restrictions.*;
import com.tesis.teamsoft.metaheuristics.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.tesis.teamsoft.config.AlgorithmConfig;

@Service
public class TeamFormationStepThreeImpl implements ITeamFormationStepThreeService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IPersonRepository personRepository;

    @Autowired
    private ILevelsRepository lvlRepository;

    @Autowired
    private ICostDistanceRepository costDistanceRepository;

    @Autowired
    private IConflictIndexRepository conflictIndexRepository;

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IPersonGroupRepository iPersonGroupRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IAssignedRoleRepository assignedRoleRepository;


    public List<TeamProposalDTO>  getTeam(TeamFormationParameters parameters, List<Long> projectsIDs, List<Long> groupIDs) throws Exception {

        parameters.setProjects(formatProjects(getUnsavedProjects(projectsIDs)));
        parameters.setSearchArea(getSearchArea(getGroups(groupIDs)));

        parameters.setMaxLevel(lvlRepository.findFirstByOrderByLevelsDesc());
        parameters.setMinLevel(lvlRepository.findFirstByOrderByLevelsAsc());
        parameters.setMaxCostDistance(costDistanceRepository.findFirstByOrderByCostDistanceDesc());
        parameters.setMaxConflictIndex(conflictIndexRepository.findFirstByOrderByWeightDesc());

        ProjectRoleState initialVoidSolution = TeamBuilder.getInitialVoidSolution(parameters); // construir una solucion inicial vacía (contendrá las personas que ya fueron asignadas a cada rol)

        List<TeamProposalDTO> teamProposal = new ArrayList<>();

        //Funciones objetivo
        ArrayList<ObjetiveFunction> objectiveFunctions = new ArrayList<>();
        objectiveFunctions.addAll(ObjetiveFunctionUtil.getObjectiveFunctions(parameters));
        //Restricciones
        ArrayList<Constrain> restrictions = new ArrayList<>();
        restrictions.addAll(getRestrictions(parameters));
        //Operador
        TeamFormationOperator operator = new TeamFormationOperator();
        //Problema
        TeamFormationProblem problem = new TeamFormationProblem();

        problem.setOperator(operator);
        problem.setState(initialVoidSolution);
        problem.setTypeSolutionMethod(parameters.getSolutionMethodOptionTeam());
        problem.setTypeProblem(Problem.ProblemType.Maximizar); //Cambiado de Maximizar a Minimizar
        problem.setFunction(objectiveFunctions);
        problem.setParameters(parameters);

        ArrayList<PersonEntity> searchArea = parameters.getSearchArea();
        TeamFormationCodification codification = new TeamFormationCodification(restrictions, problem, searchArea); //Inicializar parametros de la codificacion

        problem.setState(initialVoidSolution);
        problem.setCodification(codification);

        switch (parameters.getSolutionMethodOptionTeam()) { //Seleccion de algoritmo

            case FactoresPonderados: {
                if (ensureGeneralFactorWeightSummatory(parameters)) {

                    switch (parameters.getSolutionAlgorithm()) {
                        case 1:
                            teamProposal = buildTeamProposalTree(applyHillClimbing(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 2:
                            teamProposal = buildTeamProposalTree(applyHillClimbingRestart(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 3:
                            teamProposal = buildTeamProposalTree(applyRandomSearch(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 4:
                            teamProposal = buildTeamProposalTree(applyTabuSearch(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 5:
                            teamProposal = buildTeamProposalTree(applyAlgorithmsBriefcase(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 6:
                            teamProposal = buildTeamProposalTree(GA(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                        case 7:
                            teamProposal = buildTeamProposalTree(applySimulatedAnnealing(initialVoidSolution, problem), parameters);
                            if (teamProposal.isEmpty()) {
                                throw new IllegalArgumentException("impossible_get_proposal");
                            }
                            break;
                    }
                } else {
                    throw new IllegalArgumentException("fo_weights_most_sum_one");
                }
            }
            break;

            case MultiObjetivoPuro: {

                switch (parameters.getSolutionAlgorithm()) {

                    case 1:
                        teamProposal = buildTeamProposalTree(applyMultiobjectiveStochasticHC(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;

                    case 2:
                        teamProposal = buildTeamProposalTree(applyMultiobjectiveHCRestart(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;

                    case 3:
                        teamProposal = buildTeamProposalTree(applyMultiobjectiveHCDistance(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;

                    case 4:
                        teamProposal = buildTeamProposalTree(applyMultiobjectiveTabuSearch(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;

                    case 5:
                        teamProposal = buildTeamProposalTree(applyMultiobjectiveAlgorithmsBriefcase(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;
                    case 6:
                        teamProposal = buildTeamProposalTree(applyMOGA(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;
                    case 7:
                        teamProposal = buildTeamProposalTree(applyUMOSA(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;
                    case 8:
                        teamProposal = buildTeamProposalTree(applyMCMOSA(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;
                    case 9:
                        teamProposal = buildTeamProposalTree(applyNSGAII(initialVoidSolution, problem), parameters);
                        if (teamProposal.isEmpty()) {
                            throw new IllegalArgumentException("impossible_get_proposal");
                        }
                        break;
                }
            }
            break;
        }

        return teamProposal;
    }


    public List<ProjectRole> getProjectRolesForSaveTeam(TeamProposalDTO teamProposalDTO) {
        List<ProjectRole> projectRoles = new ArrayList<>();

        if (teamProposalDTO != null && teamProposalDTO.getProjectsProposal() != null) {
            for (ProjectDTO.ProjectTeamProposalDTO projectProposal : teamProposalDTO.getProjectsProposal()) {
                ProjectRole projectRole = new ProjectRole();

                List<ProjectEntity> allProjects = projectRepository.findAll();
                Map<Long, ProjectEntity> projectMap = allProjects.stream()
                        .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));
                ProjectEntity project = projectMap.get(projectProposal.getProject().getId());
                if (project == null) {
                    throw new RuntimeException("Project not found with ID: " + projectProposal.getProject().getId());
                }
                projectRole.setProject(project);
                List<RoleWorker> roleWorkers = new ArrayList<>();

                for (AssignedRoleDTO assignedRole : projectProposal.getAssignedRoles()) {
                    RoleWorker roleWorker = new RoleWorker();

                    // Obtener la entidad Role completa usando el ID del DTO
                    RoleEntity role = roleRepository.findById(assignedRole.getRole().getId())
                            .orElseThrow(() -> new RuntimeException("Role not found with id: " + assignedRole.getRole().getId()));
                    roleWorker.setRole(role);

                    List<PersonEntity> workers = new ArrayList<>();
                    for (PersonDTO.PersonMinimalDTO personMinimal : assignedRole.getPersons()) {
                        // Obtener la entidad PersonEntity completa usando el ID del DTO
                        PersonEntity person = personRepository.findById(personMinimal.getId())
                                .orElseThrow(() -> new RuntimeException("Person not found with id: " + personMinimal.getId()));
                        workers.add(person);
                    }
                    roleWorker.setWorkers(workers);
                    roleWorkers.add(roleWorker);
                }
                projectRole.setRoleWorkers(roleWorkers);
                projectRoles.add(projectRole);
            }
        }
        return projectRoles;
    }


    public TeamProposalDTO saveTeamProposal(TeamProposalDTO teamProposalDTO) throws Exception {
        if (teamProposalDTO == null) {
            throw new IllegalArgumentException("Team proposal is null");
        }

        // Obtener la lista de ProjectRole a partir del DTO
        List<ProjectRole> projectRoles = getProjectRolesForSaveTeam(teamProposalDTO);
        Date currentDate = new Date();

        for (ProjectRole projectRole : projectRoles) {
            // Obtener el último ciclo del proyecto (similar a como se hacía en TeamBuilder.lastProjectCycle)
            CycleEntity lastCycle = TeamBuilder.lastProjectCycle(projectRole.getProject());

            for (RoleWorker roleWorker : projectRole.getRoleWorkers()) {
                RoleEntity role = roleWorker.getRole();
                for (PersonEntity person : roleWorker.getWorkers()) {
                    // Crear y guardar la asignación
                    AssignedRoleEntity assignedRole = new AssignedRoleEntity();
                    assignedRole.setStatus(Status.ACTIVE); // o el status que corresponda
                    assignedRole.setObservation("Assigned by team formation algorithm");
                    assignedRole.setBeginDate(currentDate);
                    assignedRole.setCycles(lastCycle);
                    assignedRole.setRole(role);
                    assignedRole.setPerson(person); // Asumiendo que la entidad AssignedRoleEntity tiene una relación con PersonEntity

                    // Guardar la asignación
                    assignedRoleRepository.save(assignedRole);
                }
            }
        }

        return teamProposalDTO;
    }


    public List<TeamProposalDTO> buildTeamProposalTree(List<State> teamProposal, TeamFormationParameters parameters) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        List<TeamProposalDTO> teamsProposal = new ArrayList<>();

        if (!teamProposal.isEmpty()) {
            String format = AlgorithmConfig.getString("decimalFormat", "0.####");
            DecimalFormat df = new DecimalFormat(format);
            ArrayList<ObjetiveFunction> objectiveFunctions = new ArrayList<>(ObjetiveFunctionUtil.getObjectiveFunctions(parameters));
            TeamProposalDTO team;
            ProjectDTO.ProjectTeamProposalDTO projectNode;
            AssignedRoleDTO roleNode;
            PersonDTO.PersonMinimalDTO personNode;

            for (State state : teamProposal) {
                if (state != null) {
                    StringBuilder eval = new StringBuilder();
                    if (state.getEvaluation().size() == 1) {
                        eval.append(" ").append(objectiveFunctions.getFirst().getClass().getField("className").get(null));
                        for (int i = 1; i < objectiveFunctions.size(); i++) {
                            eval.append(" | ").append(objectiveFunctions.get(i).getClass().getField("className").get(null));
                        }
                        eval.append(": ").append(df.format(state.getEvaluation().getFirst()));
                    } else {
                        for (int i = 0; i < objectiveFunctions.size(); i++) {
                            eval.append(" ").append(objectiveFunctions.get(i).getClass().getField("className").get(null)).append(": ").append(df.format(state.getEvaluation().get(i)));
                        }
                    }
                    String formattedEval = "Propuesta: " + eval.toString();
                    team = new TeamProposalDTO(formattedEval);

                    List<Object> projects = state.getCode();

                    if (projects != null) {
                        for (Object proj : projects) {
                            ProjectRole projectRole = (ProjectRole) proj;
                            projectNode = new ProjectDTO.ProjectTeamProposalDTO(modelMapper.map(projectRole.getProject(), ProjectDTO.ProjectSimpleDTO.class));

                            for (RoleWorker pr : projectRole.getRoleWorkers()) {
                                if (pr != null) {
                                    roleNode = new AssignedRoleDTO(modelMapper.map(pr.getRole(), RoleDTO.RoleMinimalDTO.class));
                                    pr.getWorkers().addAll(pr.getFixedWorkers());
                                    for (PersonEntity p : pr.getWorkers()) {
                                        if (p != null) {
                                            personNode = modelMapper.map(p, PersonDTO.PersonMinimalDTO.class);
                                            roleNode.getPersons().add(personNode);
                                        }
                                    }
                                    projectNode.getAssignedRoles().add(roleNode);
                                }
                            }
                            team.getProjectsProposal().add(projectNode);
                        }
                    }
                    teamsProposal.add(team);
                }
            }
        }
        return teamsProposal;
    }

    public boolean ensureGeneralFactorWeightSummatory(TeamFormationParameters parametersTeam) {
        boolean isOk = true;
        float weight = 0;

        if (parametersTeam.isMaxCompetences()) {
            weight += parametersTeam.getMaxCompetencesWeight();
        }
        if (parametersTeam.isTakeWorkLoad()) {
            weight += parametersTeam.getWorkLoadWeight();
        }
        if (parametersTeam.isMinCostDistance()) {
            weight += parametersTeam.getMinCostDistanceWeight();
        }
        if (parametersTeam.isMaxInterests()) {
            weight += parametersTeam.getMaxInterestsWeight();
        }
        if (parametersTeam.isMaxProjectInterests()) {
            weight += parametersTeam.getMaxProjectInterestsWeight();
        }
        if (parametersTeam.isMinIncomp()) {
            weight += parametersTeam.getMinIncompWeight();
        }
        if (parametersTeam.isMaxBelbinRoles()) {
            weight += parametersTeam.getMaxBelbinWeight();
        }
        if (parametersTeam.isMaxMbtiTypes()) {
            weight += parametersTeam.getMaxMbtiTypesWeight();
        }
        if (parametersTeam.isMaxMultiroleTeamMembers()) {
            weight += parametersTeam.getMaxMultiroleTeamMembersWeight();
        }
        if (parametersTeam.isHomogeneousTeams()) {
            weight += parametersTeam.getHomogeneousTeamsWeight();
        }
        if (parametersTeam.isHeterogeneousTeams()) {
            weight += parametersTeam.getHeterogeneousTeamsWeight();
        }
        if (parametersTeam.isMaxSex()) {
            weight += parametersTeam.getMaxSexWeight();
        }
        if (parametersTeam.isMinSex()) {
            weight += parametersTeam.getMinSexWeight();
        }
        if (parametersTeam.isBalanceCompetences()) {
            weight += parametersTeam.getBalanceCompetenceWeight();
        }
        if (parametersTeam.isBalancePersonWorkload()) {
            weight += parametersTeam.getBalanceWorkLoadWeight();
        }
        if (parametersTeam.isBalanceCostDistance()) {
            weight += parametersTeam.getBalanceCostDistanceWeight();
        }
        if (parametersTeam.isBalanceInterests()) {
            weight += parametersTeam.getBalanceInterestWeight();
        }
        if (parametersTeam.isBalanceProjectInterests()) {
            weight += parametersTeam.getBalanceProjectInterestWeight();
        }
        if (parametersTeam.isBalanceMbtiTypes()) {
            weight += parametersTeam.getBalanceMbtiTypesWeight();
        }
        if (parametersTeam.isBalanceSynergy()) {
            weight += parametersTeam.getBalanceSynergyWeight();
        }
        if (parametersTeam.isBalanceBelbinRoles()) {
            weight += parametersTeam.getBalanceBelbinWeight();
        }
        if (parametersTeam.isBalanceMultiroleTeamMembers()) {
            weight += parametersTeam.getBalanceMultiroleTeamMembersWeight();
        }
        if (parametersTeam.isBalanceMaximizeSexFactor()) {
            weight += parametersTeam.getBalanceMaximizeSexFactorWeight();
        }
        if (parametersTeam.isBalanceMinimizeSexFactor()) {
            weight += parametersTeam.getBalanceMinimizeSexFactorWeight();
        }
        if (parametersTeam.isBalanceHeterogeneousTeams()) {
            weight += parametersTeam.getBalanceHeterogeneousTeamsNacionalityFactorWeight();
        }
        if (parametersTeam.isBalanceHomogeneousTeams()) {
            weight += parametersTeam.getBalanceHomogeneousTeamsNacionalityFactorWeight();
        }
        if (parametersTeam.isMaxReligion()) {
            weight += parametersTeam.getMaxReligionWeight();
        }
        if (parametersTeam.isMinReligion()) {
            weight += parametersTeam.getMinReligionWeight();
        }
        if (parametersTeam.isBalanceMaximizeReligion()) {
            weight += parametersTeam.getBalanceMaximizeReligionWeight();
        }
        if (parametersTeam.isBalanceMinimizeReligion()) {
            weight += parametersTeam.getBalanceMinimizeReligionWeight();
        }
        if (parametersTeam.isMaxAgeHeterogeneity()) {
            weight += parametersTeam.getMaxAgeHeterogeneityWeight();
        }
        if (parametersTeam.isMinAgeHomogeneity()) {
            weight += parametersTeam.getMinAgeHomogeneityWeight();
        }
        if (parametersTeam.isBalanceMaximizeAgeHeterogeneity()) {
            weight += parametersTeam.getBalanceMaximizeAgeHeterogeneityWeight();
        }
        if (parametersTeam.isBalanceMinimizeAgeHomogeneity()) {
            weight += parametersTeam.getBalanceMinimizeAgeHomogeneityWeight();
        }

        if (weight != 1) {
            isOk = false;
        }
        return isOk;
    }

    public List<ProjectEntity> getUnsavedProjects(List<Long> projectsIDs) {
        List<ProjectEntity> projects = new ArrayList<>();

        List<ProjectEntity> allProjects = projectRepository.findAll();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        for(Long projectsID : projectsIDs) {
            ProjectEntity project = projectMap.get(projectsID);
            projects.add(project);
        }
        return projects;
    }

    public List<ProjectRoleCompetenceTemplate> formatProjects(List<ProjectEntity> unformatedProjects) {

        List<ProjectRoleCompetenceTemplate> formatedProjectList = new ArrayList<>();

        if (!unformatedProjects.isEmpty()) {
            ProjectRoleCompetenceTemplate prct;

            for (ProjectEntity item : unformatedProjects) { //para cada proyecto
                CycleEntity lastC = TeamBuilder.lastProjectCycle(item); // obtener ultimo ciclo
                ProjectStructureEntity structure = lastC.getProjectStructure(); //obtener estructura del ultimo ciclo
                List<ProjectRolesEntity> projectRoles = structure.getProjectRolesList(); //obtener roles requeridos para desarrollar el proyecto
                List<ProjectTechCompetenceEntity> techComp = projectTechnicalRequirements(projectRoles); //obtener competencias tecnicas requeridas por el proyecto

                prct = new ProjectRoleCompetenceTemplate();
                prct.setProject(item);
                prct.setRoleCompetences(doPCTFormat(projectRoles));//dar formato a las competencias del proyecto
                formatedProjectList.add(prct); //agregar proyecto con formato prct a la lista

            }
        }
        return formatedProjectList;
    }

    public List<ProjectTechCompetenceEntity> projectTechnicalRequirements(List<ProjectRolesEntity> projectRoles) {
        List<ProjectTechCompetenceEntity> out = new ArrayList<>();

        for (ProjectRolesEntity projectRole : projectRoles) {
            out.addAll(projectRole.getProjectTechCompetenceList());
        }

        return out;
    }

    public List<ProjectCompetenceTemplate> doPCTFormat(List<ProjectRolesEntity> projectRoles) {
        List<ProjectCompetenceTemplate> pctList = new ArrayList<>();

        ProjectCompetenceTemplate projectCT;
        for (ProjectRolesEntity prole : projectRoles) {  //para cada rol de proyectos
            projectCT = new ProjectCompetenceTemplate();
            projectCT.setRole(prole.getRole()); //declarar rol del ProjectCompetenceTemplate

            List<RoleCompetitionEntity> roleCompetitionList = prole.getRole().getRoleCompetitionList(); //obtener competencias genericas para el rol
            List<ProjectTechCompetenceEntity> roleTechCompetenceList = prole.getProjectTechCompetenceList(); //obtener competencias tecnicas para el rol en el proyecto

            List<CompetencesTemplate> genComp = doCTFormat(roleCompetitionList); //dar formato a las competencias genericas
            List<CompetencesTemplate> techComp = doCTFormat(roleTechCompetenceList);//dar formato a las competencias tecnicas

            projectCT.setGenCompetences(genComp); // asignar competencias con nuevo formato
            projectCT.setTechCompetences(techComp);

            pctList.add(projectCT);
        }

        return pctList;
    }

    public List<CompetencesTemplate> doCTFormat(List competences) {
        List<CompetencesTemplate> compTemplateList = new ArrayList<>();

        CompetencesTemplate cTemplate;

        for (Object item : competences) { //para cada competencia
            cTemplate = new CompetencesTemplate();

            if (item instanceof RoleCompetitionEntity) { //se obtienen competencias genericas
                cTemplate.setCompetence(((RoleCompetitionEntity) item).getCompetence());
                cTemplate.setCompetenceImportance(((RoleCompetitionEntity) item).getCompetenceImportance());
                cTemplate.setMinLevel(((RoleCompetitionEntity) item).getLevel());
            }

            if (item instanceof ProjectTechCompetenceEntity) {//se obtienen competencias tecnicas
                cTemplate.setCompetence(((ProjectTechCompetenceEntity) item).getCompetence());
                cTemplate.setCompetenceImportance(((ProjectTechCompetenceEntity) item).getCompetenceImportance());
                cTemplate.setMinLevel(((ProjectTechCompetenceEntity) item).getLevel());
            }
            compTemplateList.add(cTemplate);
        }

        return compTemplateList;
    }

    public ArrayList<PersonEntity> getSearchArea(List<PersonGroupEntity> groups) {
        ArrayList<PersonEntity> searchArea = new ArrayList<>();
        List<PersonEntity> source = personRepository.findAll();

        for (PersonEntity item : source) { //para cada persona
            if (item.getStatus() != null && item.getStatus().equals("ACTIVE")) { //si esta activa
                int i = 0;
                while (i < groups.size()) // para cada grupo
                {
                    if (item.getGroup().equals(groups.get(i))) //si la persona pertenece
                    {
                        searchArea.add(item);
                    }
                    i++;
                }
            }

        }
        return searchArea;
    }

    public List<PersonGroupEntity> getGroups(List<Long> groupsIDs) {
        List<PersonGroupEntity> groups = new ArrayList<>();

        for (Long id : groupsIDs) {
            PersonGroupEntity group = iPersonGroupRepository.findById(id).
                    orElseThrow(() -> new RuntimeException("No group found with id " + id));

            groups.add(group);
        }

        return groups;
    }

    public List<Constrain> getRestrictions(TeamFormationParameters parameters) {
        List<Constrain> restrictions = new ArrayList<>();

        if (parameters != null) {
            restrictions.add(new WorkerNotRepeatedInSameRole());//obligatoria, evitar que se repita la misma persona en un rol
            restrictions.add(new WorkerRemovedFromRole()); //obligatoria, que la persona no este asignada a un ciclo anterior activo del proyecto
            if (parameters.isConfRole()) {
                restrictions.add(new MaximumRoles()); //Que una persona no pueda desempeÃ±ar mÃ¡s roles que los definidos en la interfaz
            }
            if (parameters.isOnlyOneProject()) { //una persona solo puede ser asignada a un proyecto
                restrictions.add(new DifferentPersonByProject());
            }
            if (parameters.isCanBeProjectBoss()) { //una persona solo puede ser asignada a un proyecto
                restrictions.add(new IsWorkerAssigned());
            }
            if (parameters.isCanBeProjectBoss()) { //una persona solo puede ser jefe de un projecto
                restrictions.add(new IsProjectBoss());
            }
            if (parameters.isConfAllGroup()) { //una persona solo puede ser jefe de un projecto
                restrictions.add(new PersonPerGroupAssigned());
            }
            if (parameters.isTakeCustomPersonWorkLoad()) { //la persona no puede tener una carga de trabajo superior a la definida
                if (parameters.getMaxRoleLoad() != null) {
                    restrictions.add(new MaxWorkload(parameters));
                }
            }
            if (parameters.isMaxCompetencesByProject()) { // cada persona debe tener los niveles de competencias seleccionados
                restrictions.add(new AllCompetitionLevels(parameters));
            }
            if (parameters.isAnyIncompatibility()) {
                restrictions.add(new IncompatibleWorkers());
            }
            if (parameters.isAnySelectedIncompatibility()) {
                if (parameters.getSWI() != null && !parameters.getSWI().isEmpty()) {
                    restrictions.add(new IncompatibleSelectedWorkers(parameters));
                }
                // incompatibilidades de la visual ede conf equipo
                if (parameters.getSRI() != null && !parameters.getSRI().isEmpty()) {
                    restrictions.add(new IncompatibleRoles(parameters));
                }
            }
            //sobre las caracteristicas psicologicas considerar
            if (parameters.isAllBelbinRoles()) { //exigir presencia de todos los roles de belbin
                restrictions.add(new AllBelbinRoles());
            }
            if (parameters.isDemandNBrains()) { //demndar una cantidad de personas con rol cerebro por equipo
                restrictions.add(new ExistCerebro(parameters));
            }
            if (parameters.isBalanceBelbinCategories()) { //balancear categorias de belbin
                restrictions.add(new isBalanced(parameters));
            }
            if (parameters.isAllBelbinCategories()) { //exigir precensia de todas las categorias de roles de belbin
                restrictions.add(new AllBelbinCategories());
            }
            if (parameters.isMinimunRoles()) {
                restrictions.add(new MinimumRoles());
            }
            if (parameters.isBossNeedToBeAssignedToAnotherRoles()) {
                restrictions.add(new BossNeedToBeAssignedToAnotherRole());
            }
        }
        return restrictions;
    }

    public void configStrategy(TeamFormationProblem problem) {
        // Configurando elementos comunes de la estrategia
        Strategy.getStrategy().setStopexecute(new StopExecute());
        Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
        Strategy.getStrategy().setProblem(problem);
        Strategy.getStrategy().calculateTime = true;//Boolean.parseBoolean(ResourceBundle.getBundle("/algorithmConf").getString("calculateTime"));
    }

    public State prepareSolution() {
        List<State> states = Strategy.getStrategy().listStates;
        State bestState = null;
        int i = 0;
        boolean found = false;

        while (i < states.size() && !found) {
            if (Strategy.getStrategy().getProblem().getCodification().validState(states.get(i))) {
                found = true;
                bestState = states.get(i);
            }
            i++;
        }

        while (i < states.size()) {
            if (Strategy.getStrategy().getProblem().getCodification().validState(states.get(i))) {
                if (states.get(i).getEvaluation().getFirst() > bestState.getEvaluation().getFirst()) {
                    bestState = states.get(i);
                }
            }
            i++;
        }
        return bestState;
    }

    public List<State> applyHillClimbing(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;

                Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.HillClimbing);
                State bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                    //SaveTxt.writeStatesMono();
//                    long time = System.currentTimeMillis() / 1000;
                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }
        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el metodo HillClimbing
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyHillClimbingRestart(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                HillClimbingRestart.count = AlgorithmConfig.getInt("HillClimbingRestartCount", 10);

                Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.HillClimbingRestart);
                State bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                    //SaveTxt.writeStatesMono();

                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                        //SaveTxt.writeStatesMono();
                    }
                }
                Strategy.destroyExecute();
            }

        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el metodo RandomSearch
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyRandomSearch(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;

                Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.RandomSearch);
                State bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }

        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el metodo TabuSearch
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyTabuSearch(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                TabuSolutions.maxelements = AlgorithmConfig.getInt("TabuSolutionsMaxelements", 10);

                Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.TabuSearch);
                State bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }

        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el portafolio de algoritmos
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyAlgorithmsBriefcase(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException {

        List<State> sol = null;
        if (initialSolution != null) {

            int iterations = AlgorithmConfig.getInt("iterations", 100);
            int executions = AlgorithmConfig.getInt("executions", 1);
            for (int i = 0; i < executions; i++) {
                //RS,RE,RL,EE,GA,BT,BA,ECR,EDA,EC,LU
                configurePA(iterations, false, false, false, false, false, true, true, true, false, true, false);
                Strategy.getStrategy().setStopexecute(new StopExecute());
                Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                Strategy.getStrategy().setProblem(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().calculateTime = true;
                Strategy.getStrategy().saveListStates = true;
                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiGenerator);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
            Strategy.destroyExecute();
        }
        return sol;
    }
    //NSGAII

    private List<State> applyNSGAII(ProjectRoleState initialSolution, TeamFormationProblem problem) {
        int ITERATIONS = AlgorithmConfig.getInt("iterations", 100);
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();
            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                //Configurar el algoritmo dentro del BiCIAM
                Strategy.getStrategy().setStopexecute(new StopExecute());
                Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                NSGAII.countRef = AlgorithmConfig.getInt("CountRef", 100);
                NSGAII.selectionType = SelectionType.TournamentSelection;

                NSGAII.crossoverType = CrossoverType.GenericCrossover;
                NSGAII.mutationType = MutationType.GenericMutation;
                Strategy.getStrategy().calculateTime = true;
                NSGAII.PM = AlgorithmConfig.getFloat("PM", 0.9f);
                NSGAII.PC = AlgorithmConfig.getFloat("PC", 0.5f);

                try {
                    Strategy.getStrategy().executeStrategy(ITERATIONS, 1, GeneratorType.RandomSearch);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }
        }
        return sol;
    }

    //MCMOSA
    public List<State> applyMCMOSA(ProjectRoleState initialSolution, TeamFormationProblem problem) {

        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;
                MultiCaseSimulatedAnnealing.countIterationsT = 100;
                MultiCaseSimulatedAnnealing.alpha = 0.9;
                MultiCaseSimulatedAnnealing.tfinal = 0.0;
                MultiCaseSimulatedAnnealing.tinitial = 20.0;
                String format = AlgorithmConfig.getString("decimalFormat", "0.####");
                DecimalFormat df = new DecimalFormat(format);

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiCaseSimulatedAnnealing);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();

            }
        }
        return sol;
    }

    //UMOSA
    public List<State> applyUMOSA(ProjectRoleState initialSolution, TeamFormationProblem problem) {

        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();
            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;
                UMOSA.countIterationsT = 100;
                UMOSA.alpha = 0.9;
                UMOSA.tfinal = 0.0;
                UMOSA.tinitial = 20.0;
                String format = AlgorithmConfig.getString("decimalFormat", "0.####");
                DecimalFormat df = new DecimalFormat(format);

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.UMOSA);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }
                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();

            }
        }
        return sol;
    }
    //*Mogaaa

    public List<State> applyMOGA(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NoSuchFieldException {
        int ITERATIONS = AlgorithmConfig.getInt("iterations", 100);
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();
            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                //Configurar el algoritmo dentro del BiCIAM
                Strategy.getStrategy().setStopexecute(new StopExecute());
                Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                MOGA.countRef = 100;
                MOGA.selectionType = SelectionType.TournamentSelection;
                MOGA.crossoverType = CrossoverType.GenericCrossover;
                MOGA.mutationType = MutationType.GenericMutation;
                MOGA.replaceType = ReplaceType.GenerationalReplace;
                Strategy.getStrategy().calculateTime = true;
                MOGA.PM = AlgorithmConfig.getFloat("PM", 0.9f);
                MOGA.PC = AlgorithmConfig.getFloat("PC", 0.5f);
                try {
                    Strategy.getStrategy().executeStrategy(ITERATIONS, 1, GeneratorType.RandomSearch);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }
                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }
        }
        return sol;

    }

    public List<State> applySimulatedAnnealing(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NoSuchFieldException {

        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;

                SimulatedAnnealing.alpha = 0.9;
                SimulatedAnnealing.tinitial = 20.0;
                SimulatedAnnealing.tfinal = 0.0;
                SimulatedAnnealing.countIterationsT = 100;
                SimulatedAnnealing.saveRestart = true;
                Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.SimulatedAnnealing);

                State bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }

        }
        return sol;
    }

    public void configurePA(int countMaxIterations, boolean RS, boolean RE, boolean RL, boolean EE, boolean GA, boolean BT, boolean BA, boolean ECR, boolean EDA, boolean EC, boolean LU) throws IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException {
        Strategy.getStrategy().initializeGenerators();
        List<Generator> gene = new ArrayList<>();
        if (RS) {
            SimulatedAnnealing sa = new SimulatedAnnealing();
            SimulatedAnnealing.alpha = 0.9;
            SimulatedAnnealing.tinitial = 20.0;
            SimulatedAnnealing.tfinal = 0.0;
            SimulatedAnnealing.countIterationsT = 100;
            SimulatedAnnealing.saveRestart = true;
            gene.add(sa);
        }
        if (RE) {
            double chi = 0.75;
            double omega1 = -2462.822;
            double tempInitial1 = omega1 / Math.log(chi);
            SimulatedAnnealingExponential1 rs = new SimulatedAnnealingExponential1();
            SimulatedAnnealingExponential1.tinitial = tempInitial1;
            SimulatedAnnealingExponential1.tfinal = 0.0;
            double A1 = (tempInitial1 - rs.tfinal) * (10000 + 1) / 10000;
            double B1 = tempInitial1 - A1;
            SimulatedAnnealingExponential1.valueA = A1;
            SimulatedAnnealingExponential1.valueB = B1;
            SimulatedAnnealingExponential1.countIterationsT = 50;
            gene.add(rs);
        }
        if (RL) {
            double chi = 0.75;
            double omega1 = -2462.822;
            double tempInitial1 = omega1 / Math.log(chi);
            SimulatedAnnealingLinear rl = new SimulatedAnnealingLinear();
            SimulatedAnnealingLinear.t0 = tempInitial1;
            SimulatedAnnealingLinear.tinitial = tempInitial1;
            SimulatedAnnealingLinear.countIterationsT = 50;
            gene.add(rl);
        }
        if (EE) {
            EvolutionStrategies ee = new EvolutionStrategies();
            EvolutionStrategies.countRef = 10;
            EvolutionStrategies.PM = 0.9;
            EvolutionStrategies.selectionType = SelectionType.TruncationSelection;
            EvolutionStrategies.mutationType = MutationType.GenericMutation;
            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
            EvolutionStrategies.truncation = 5;
            gene.add(ee);
        }
        if (GA) {
            GeneticAlgorithm ag = new GeneticAlgorithm();
            GeneticAlgorithm.countRef = 10;
            GeneticAlgorithm.PC = 0.5;
            GeneticAlgorithm.PM = 0.9;
            GeneticAlgorithm.selectionType = SelectionType.TournamentSelection;
            GeneticAlgorithm.crossoverType = CrossoverType.GenericCrossover;
            GeneticAlgorithm.mutationType = MutationType.GenericMutation;
            GeneticAlgorithm.replaceType = ReplaceType.SteadyStateReplace;
            GeneticAlgorithm.truncation = 5;
            gene.add(ag);
        }
        if (BT) {
            TabuSearch ts = new TabuSearch();
            TabuSolutions.maxelements = AlgorithmConfig.getInt("TabuSolutionsMaxelements", 10);
            gene.add(ts);
        }
        if (EC) {
            HillClimbing hc = new HillClimbing();
            gene.add(hc);
        }
        if (BA) {
            RandomSearch rs = new RandomSearch();
            gene.add(rs);
        }
        if (ECR) {
            HillClimbingRestart hcr = new HillClimbingRestart();
            hcr.count = AlgorithmConfig.getInt("HillClimbingRestartCount", 10);
            gene.add(hcr);
        }
        MultiGenerator.setListGenerators(gene);
    }

    public List<State> applyMultiobjectiveAlgorithmsBriefcase(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException, Exception {
        List<State> noDominadas = new ArrayList<State>();
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        int executions = AlgorithmConfig.getInt("executions", 1);
        String fichero = "multiAlgorithmsBriefcase.txt";
        for (int i = 0; i < executions; i++) {
            //Configurar el algoritmo, dentro de BiCIAM
            Strategy.getStrategy().setStopexecute(new StopExecute());
            Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
            Strategy.getStrategy().setProblem(problem);
            Strategy.getStrategy().saveListBestStates = true;
            Strategy.getStrategy().calculateTime = true;
            Strategy.getStrategy().saveListStates = true;

            //Aqui va la configuracion espeficia del PA multiobjetivo
            //ECMO, ECMOR, ECMOD, BTMO, RSMO, RSMOU, NSGAII, MOGA, MOEADDE
            configurePAMO(iterations, true, true, false, true, false, false, false, false, false);
            createPlans();
            Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiobjectiveMultiGenerator); //cambia aqui el generator type por el multigenerator.
            noDominadas = Strategy.getStrategy().listRefPoblacFinal;
            Strategy.destroyExecute();
            MultiobjectiveMultiGenerator.destroyMultiobjectiveMultiGenerator();
        }
        return noDominadas;
    }

    public void configurePAMO(int countIterations, boolean ECMO, boolean ECMOR, boolean ECMOD, boolean BTMO, boolean RSMO, boolean RSMOU, boolean NSGAII, boolean MOGA, boolean MOEADDE)
            throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //ECMO, ECMOR, ECMOD, BTMO, RSMO, RSMOU, NSGAII, MOGA, MOEADDE
        Strategy.getStrategy().initializeGenerators();
        List<Generator> gene = new ArrayList<Generator>();
        NSGAII nsga = new NSGAII();
        MOGA moga = new MOGA();
        MOEADDE moeadde = new MOEADDE();
        if (ECMO) {
            MultiobjectiveStochasticHillClimbing ecmo = new MultiobjectiveStochasticHillClimbing();
            gene.add(ecmo);
        }
        if (ECMOR) {
            MultiobjectiveHillClimbingRestart ecmor = new MultiobjectiveHillClimbingRestart();
            ecmor.sizeNeighbors = 2;
            gene.add(ecmor);
        }
        if (ECMOD) {
            MultiobjectiveHillClimbingDistance ecmod = new MultiobjectiveHillClimbingDistance();
            gene.add(ecmod);
        }
        if (BTMO) {
            MultiobjectiveTabuSearch btmo = new MultiobjectiveTabuSearch();
            gene.add(btmo);
        }
        if (RSMO) {
            MultiCaseSimulatedAnnealing rsmo = new MultiCaseSimulatedAnnealing();
            rsmo.countIterationsT = 30;
            rsmo.alpha = 0.9;
            rsmo.tfinal = 0.0;
            rsmo.tinitial = 20.0;
            gene.add(rsmo);
        }
        if (RSMOU) {
            UMOSA rsmou = new UMOSA();
            rsmou.countIterationsT = 30;
            rsmou.alpha = 0.9;
            rsmou.tfinal = 0.0;
            rsmou.tinitial = 20.0;
            gene.add(rsmou);
        }
        if (NSGAII) {
            nsga.countRef = 20; //cantidad de individuos 20
            nsga.selectionType = SelectionType.TournamentSelection;
            nsga.crossoverType = CrossoverType.GenericCrossover;
            nsga.mutationType = MutationType.GenericMutation;
            nsga.PM = 0.5;
            nsga.PC = 0.9;
            gene.add(nsga);
        }
        if (MOGA) {
            moga.countRef = 50; //cantidad de individuos 100
            moga.selectionType = SelectionType.TournamentSelection;
            moga.crossoverType = CrossoverType.GenericCrossover;
            moga.mutationType = MutationType.GenericMutation;

            //moga.replaceType = ReplaceType.GenerationalReplace;
            moga.replaceType = ReplaceType.SteadyStateReplace;
            moga.PM = 0.9;
            moga.PC = 0.5;
            gene.add(moga);
        }
        if (MOEADDE) {
            moeadde.countRef = 100; //cantidad de individuos 100
            moeadde.crossoverType = CrossoverType.OnePointCrossover;
            moeadde.mutationType = MutationType.TowPointsMutation;
            moeadde.PS = 0.7;
            moeadde.PC = 0.6;
            moeadde.PM = 0.9;
            moeadde.T = (int) (0.1 * moeadde.countRef);
            moeadde.Nr = (int) (0.1 * moeadde.countRef);
            gene.add(moeadde);
        }
        MultiobjectiveMultiGenerator.setListGenerators(gene);

    }

    public List<State> GA(ProjectRoleState initialSolution, TeamFormationProblem problem) throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException {

        int ITERATIONS = AlgorithmConfig.getInt("iterations", 100);
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);

        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                State bestState = null;
                Strategy.getStrategy().setStopexecute(new StopExecute());
                Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;

                //Configuracion del algoritmo genetico
                //cantidad de individuos
                GeneticAlgorithm.countRef = AlgorithmConfig.getInt("CountRef", 100);
                //Probabilidad de cruzamiento o combinacion
                GeneticAlgorithm.PC = AlgorithmConfig.getFloat("PC", 0.5f);
                //Probabilidad de mutacion
                GeneticAlgorithm.PM = AlgorithmConfig.getFloat("PM", 0.9f);

                GeneticAlgorithm.selectionType = SelectionType.TournamentSelection;
                GeneticAlgorithm.truncation = AlgorithmConfig.getInt("GeneticAlgorithmTruncation", 5);
                GeneticAlgorithm.crossoverType = CrossoverType.GenericCrossover;
                GeneticAlgorithm.mutationType = MutationType.GenericMutation;
                GeneticAlgorithm.replaceType = ReplaceType.SteadyStateReplace;

                Strategy.getStrategy().executeStrategy(ITERATIONS, 1, GeneratorType.RandomSearch);

                bestState = Strategy.getStrategy().getBestState();

                if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                    sol.add(bestState);
                } else {
                    bestState = prepareSolution();
                    if (bestState != null) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }
        }
        return sol;
    }

    public void createPlans() throws Exception {
        /*List<Plan> plans = new ArrayList<Plan>();

        ArrayList<Float> weights = new ArrayList<>();
        ArrayList<Float> taxs = new ArrayList<>();
        ArrayList<Float> weights2 = new ArrayList<>();

        weights.add(50f);

        weights.add(50f);

        weights.add(50f);

        weights2.add(50f);
        weights2.add(0.0f);
        weights2.add(0.0f);
        weights2.add(0.0f);
        weights2.add(0.0f);
        weights2.add(10f);
        weights2.add(0.0f);
        weights2.add(30f);
        weights2.add(0.0f);

        for (int i = 0; i < 9; i++) {

            taxs.add(0.1f);
        }

        Plan plan1 = new Plan(1, 1000, taxs, 10, weights, SelectionTypePortfolio.Roulette);
        //Plan plan2 = new Plan(5001, 10000, taxs, 10, 0.2f, weights2);
        plans.add(plan1);
        //plans.add(plan2);

        if (MultiobjectiveMultiGenerator.validateIterations(plans, 1000)) {
            MultiobjectiveMultiGenerator.setPlans(plans);
        } else {
            throw new Exception("No coinciden las iteraciones");
        }*/
    }

    /**
     * Mejorar solucion aplicando el metodo MultiobjectiveStochasticHillClimbing
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyMultiobjectiveStochasticHC(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException {

        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiobjectiveStochasticHillClimbing);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();

            }
        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el metodo MultiobjectiveHCRestart
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyMultiobjectiveHCRestart(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException {

        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);

        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();

            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;
                MultiobjectiveHillClimbingRestart.sizeNeighbors = AlgorithmConfig.getInt("MultiobjectiveHCRestartSizeNeighbors", 2);

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiobjectiveHillClimbingRestart);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();

            }
        }
        return sol;
    }

    /**
     * Mejorar solucion aplicando el metodo MultiobjectiveHCDistance
     *
     * @param initialSolution
     * @param problem
     * @return
     * @throws java.io.IOException
     */
    public List<State> applyMultiobjectiveHCDistance(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();
            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;
                MultiobjectiveHillClimbingDistance.sizeNeighbors = AlgorithmConfig.getInt("MultiobjectiveHCDistanceSizeNeighbors", 2);

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiobjectiveHillClimbingDistance);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();

            }
        }
        return sol;
    }

    public List<State> applyMultiobjectiveTabuSearch(ProjectRoleState initialSolution, TeamFormationProblem problem) throws IOException {
        int EXECUTIONS = AlgorithmConfig.getInt("executions", 1);
        int iterations = AlgorithmConfig.getInt("iterations", 100);
        List<State> sol = null;
        if (initialSolution != null) {
            sol = new ArrayList<>();
            for (int i = 0; i < EXECUTIONS; i++) {
                configStrategy(problem);
                Strategy.getStrategy().saveListBestStates = true;
                Strategy.getStrategy().saveListStates = true;
                Strategy.getStrategy().saveFreneParetoMonoObjetivo = true;
                TabuSolutions.maxelements = AlgorithmConfig.getInt("MultiobjectiveTabuSolutionsMaxelements", 10);
                String format = AlgorithmConfig.getString("decimalFormat", "0.####");
                DecimalFormat df = new DecimalFormat(format);

                try {
                    Strategy.getStrategy().executeStrategy(iterations, 1, GeneratorType.MultiobjectiveTabuSearch);
                } catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Logger.getLogger(TeamFormationStepThreeImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                List<State> noDominadas = Strategy.getStrategy().listRefPoblacFinal;
                State bestState = Strategy.getStrategy().getBestState();

                //Mostrando las soluciones no dominadas y guardando soluciones en el fichero de texto
                for (State noDominada : noDominadas) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(noDominada)) {
                        sol.add(noDominada);
                    }
                }

                if (sol.isEmpty()) {
                    if (Strategy.getStrategy().getProblem().getCodification().validState(bestState)) {
                        sol.add(bestState);
                    }
                }
                Strategy.destroyExecute();
            }
        }
        return sol;
    }
}

