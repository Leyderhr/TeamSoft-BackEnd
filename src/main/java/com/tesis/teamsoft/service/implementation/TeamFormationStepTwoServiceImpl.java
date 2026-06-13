package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.metaheuristics.auxiliary.*;
import com.tesis.teamsoft.metaheuristics.operator.TeamBuilder;
import com.tesis.teamsoft.metaheuristics.operator.TeamFormationOperator;
import com.tesis.teamsoft.metaheuristics.restrictions.*;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.pojos.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.ITeamFormationStepTwoService;
import lombok.RequiredArgsConstructor;
import metaheurictics.strategy.Strategy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.extension.FactoresPonderados;
import problem.extension.TypeSolutionMethod;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamFormationStepTwoServiceImpl implements ITeamFormationStepTwoService {

    private final ModelMapper modelMapper;
    private final TeamFormationStepThreeImpl stepThreeService; // para reutilizar métodos existentes
    private final IProjectRepository projectRepository;
    private final IRoleRepository roleRepository;

    @Override
    public BossProposalDTO.BossProposalResponseDTO getBossProposals(BossRequestDTO.BossProposalRequestDTO request) throws Exception {
        TeamFormationParameters parameters = request.getTeamFormationParameters();

        if (!stepThreeService.ensureGeneralFactorWeightSummatory(parameters)) {
            throw new IllegalArgumentException("La suma de los pesos de las funciones objetivo debe ser 1.");
        }

        stepThreeService.hydrateFixedWorkers(parameters);

        List<ProjectEntity> projects = projectRepository.findAllById(request.getProjectIDs());
        if (projects.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron proyectos con los IDs proporcionados.");
        }
        List<ProjectRoleCompetenceTemplate> formatedProjects = stepThreeService.formatProjects(projects);
        parameters.setProjects(formatedProjects);

        List<PersonEntity> searchArea = stepThreeService.getSearchArea(request.getGroupIDs());
        parameters.setSearchArea(new ArrayList<>(searchArea));

        parameters.setMaxLevel(stepThreeService.getLvlRepository().findFirstByOrderByLevelsDesc());
        parameters.setMinLevel(stepThreeService.getLvlRepository().findFirstByOrderByLevelsAsc());
        parameters.setMaxCostDistance(stepThreeService.getCostDistanceRepository().findFirstByOrderByCostDistanceDesc());
        parameters.setMaxConflictIndex(stepThreeService.getConflictIndexRepository().findFirstByOrderByWeightDesc());

        BossProposalDTO.BossProposalResponseDTO response = new BossProposalDTO.BossProposalResponseDTO();
        response.setProposals(new ArrayList<>());

        for (ProjectEntity project : projects) {
            CycleEntity lastCycle = TeamBuilder.lastProjectCycle(project);
            ProjectStructureEntity structure = lastCycle.getProjectStructure();
            RoleEntity bossRole = structure.getProjectRolesList().stream()
                    .map(ProjectRolesEntity::getRole)
                    .filter(RoleEntity::isBoss)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró un rol de jefe en la estructura del proyecto " + project.getId()));

            List<BossProposalDTO.BossCandidateDTO> candidates =
                    evaluateCandidatesForRole(project, bossRole, parameters, searchArea);
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException(
                        "No se pudo generar ninguna propuesta de jefe para el proyecto " + project.getProjectName());
            }

            BossProposalDTO.ProjectBossDTO projectBoss = new BossProposalDTO.ProjectBossDTO();
            projectBoss.setProject(modelMapper.map(project, ProjectDTO.ProjectSimpleDTO.class));
            projectBoss.setRole(modelMapper.map(bossRole, RoleDTO.RoleMinimalDTO.class));
            projectBoss.setCandidates(candidates);
            response.getProposals().add(projectBoss);
        }

        return response;
    }

    @Override
    public MembersProposalDTO.MemberProposalResponseDTO getMemberProposals(MembersRequestDTO.MemberProposalRequestDTO request) throws Exception {
        TeamFormationParameters parameters = request.getTeamFormationParameters();

        // 1. Validar pesos
        if (!stepThreeService.ensureGeneralFactorWeightSummatory(parameters)) {
            throw new IllegalArgumentException("La suma de los pesos de las funciones objetivo debe ser 1.");
        }

        stepThreeService.hydrateFixedWorkers(parameters);

        // 2. Cargar el proyecto y el rol
        ProjectEntity project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + request.getProjectId()));
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRoleId()));

        // 3. Formatear solo este proyecto (para tener sus competencias)
        List<ProjectRoleCompetenceTemplate> formatedProjects = stepThreeService.formatProjects(Collections.singletonList(project));
        parameters.setProjects(formatedProjects);

        // 4. Área de búsqueda
        List<PersonEntity> searchArea = stepThreeService.getSearchArea(request.getGroupIDs());
        parameters.setSearchArea(new ArrayList<>(searchArea));

        // 5. Niveles extremos
        parameters.setMaxLevel(stepThreeService.getLvlRepository().findFirstByOrderByLevelsDesc());
        parameters.setMinLevel(stepThreeService.getLvlRepository().findFirstByOrderByLevelsAsc());
        parameters.setMaxCostDistance(stepThreeService.getCostDistanceRepository().findFirstByOrderByCostDistanceDesc());
        parameters.setMaxConflictIndex(stepThreeService.getConflictIndexRepository().findFirstByOrderByWeightDesc());

        // 6. Generar candidatos
        List<MembersProposalDTO.MemberCandidateDTO> candidates =
                evaluateCandidatesForMemberRole(project, role, parameters, searchArea);
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No se pudo generar ninguna propuesta de miembro para el rol dado.");
        }

        MembersProposalDTO.MemberProposalResponseDTO response = new MembersProposalDTO.MemberProposalResponseDTO();
        response.setProject(modelMapper.map(project, ProjectDTO.ProjectSimpleDTO.class));
        response.setRole(modelMapper.map(role, RoleDTO.RoleMinimalDTO.class));
        response.setCandidates(candidates);

        return response;
    }

    /**
     * Evalúa cada persona del área de búsqueda para el rol de jefe en un proyecto.
     */
    private List<BossProposalDTO.BossCandidateDTO> evaluateCandidatesForRole(
            ProjectEntity project, RoleEntity role, TeamFormationParameters parameters,
            List<PersonEntity> searchArea) throws Exception {

        return evaluateCandidatesGeneric(project, role, parameters, searchArea).stream()
                .map(c -> {
                    BossProposalDTO.BossCandidateDTO dto = new BossProposalDTO.BossCandidateDTO();
                    dto.setPerson(modelMapper.map(c.getPerson(), PersonDTO.PersonMinimalDTO.class));
                    dto.setEvaluation(c.getEvaluation());
                    return dto;
                }).collect(Collectors.toList());
    }

    /**
     * Evalúa cada persona del área de búsqueda para un rol de miembro en un proyecto.
     */
    private List<MembersProposalDTO.MemberCandidateDTO> evaluateCandidatesForMemberRole(
            ProjectEntity project, RoleEntity role, TeamFormationParameters parameters,
            List<PersonEntity> searchArea) throws Exception {

        return evaluateCandidatesGeneric(project, role, parameters, searchArea).stream()
                .map(c -> {
                    MembersProposalDTO.MemberCandidateDTO dto = new MembersProposalDTO.MemberCandidateDTO();
                    dto.setPerson(modelMapper.map(c.getPerson(), PersonDTO.PersonMinimalDTO.class));
                    dto.setEvaluation(c.getEvaluation());
                    return dto;
                }).collect(Collectors.toList());
    }

    /**
     * Método genérico que evalúa a todos los candidatos del área de búsqueda para un rol en un proyecto.
     * Retorna una lista de CandidateDTO internos (sin especializar).
     */
    private List<CandidateDTO> evaluateCandidatesGeneric(ProjectEntity project, RoleEntity role,
                                                         TeamFormationParameters parameters,
                                                         List<PersonEntity> searchArea) throws Exception {
        // Construir solución inicial vacía (contiene todos los proyectos y roles)
        ProjectRoleState initialVoidSolution = TeamBuilder.getInitialVoidSolution(parameters);

        // Configurar el problema
        TeamFormationProblem problem = new TeamFormationProblem();
        problem.setOperator(new TeamFormationOperator());
        problem.setState(initialVoidSolution);
        problem.setMethod(new FactoresPonderados());
        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        problem.setTypeProblem(Problem.ProblemType.Maximizar);
        problem.setParameters(parameters);

        Strategy.getStrategy().setProblem(problem);

        ArrayList<ObjetiveFunction> objectiveFunctions = new ArrayList<>(ObjetiveFunctionUtil.getObjectiveFunctions(parameters));
        problem.setFunction(objectiveFunctions);

        List<Constrain> restrictions = stepThreeService.getRestrictions(parameters);
        TeamFormationCodification codification = new TeamFormationCodification(
                new ArrayList<>(restrictions), problem, new ArrayList<>(searchArea));
        problem.setCodification(codification);

        List<CandidateDTO> candidates = new ArrayList<>();

        // Recorrer las estructuras de proyecto/rol en la solución para encontrar el rol objetivo
        List<Object> solutionProjects = initialVoidSolution.getCode();
        for (Object obj : solutionProjects) {
            ProjectRole projectRole = (ProjectRole) obj;
            if (projectRole.getProject().getId().equals(project.getId())) {
                for (RoleWorker rw : projectRole.getRoleWorkers()) {
                    if (rw.getRole().getId().equals(role.getId())) {
                        // Se necesitan al menos 1 trabajador para este rol
                        if (rw.getNeededWorkers() > 0) {
                            // Probar cada persona
                            for (PersonEntity p : searchArea) {
                                rw.getWorkers().clear();
                                rw.getWorkers().add(p);
                                boolean valid = codification.validState(initialVoidSolution);
                                if (valid) {
                                    problem.Evaluate(initialVoidSolution);
                                    float evaluation = problem.getState().getEvaluation().get(0).floatValue();
                                    candidates.add(new CandidateDTO(p, evaluation));
                                }
                            }
                        }
                        // Limpiar después
                        rw.getWorkers().clear();
                    }
                }
            }
        }

        // Ordenar de mayor a menor evaluación
        candidates.sort(Comparator.comparingDouble(CandidateDTO::getEvaluation).reversed());
        return candidates;
    }

    /**
     * DTO privado para almacenar temporalmente el resultado de evaluación.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class CandidateDTO {
        private PersonEntity person;
        private float evaluation;
    }
}