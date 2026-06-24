package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.pojos.ProjectStructureInfo;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {


    private final IProjectRepository projectRepository;
    private final IClientRepository clientRepository;
    private final ICountyRepository countyRepository;
    private final IProjectStructureRepository projectStructureRepository;
    private final IPersonRepository personRepository;
    private final IRoleRepository roleRepository;
    private final IRoleEvaluationRepository roleEvaluationRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<ProjectDTO.ProjectResponseDTO> saveProjects(List<ProjectDTO.ProjectCreateDTO> projectDTOs) {
        duplicatedNames(projectDTOs);
        List<ProjectEntity> projects = new ArrayList<>();

        for(ProjectDTO.ProjectCreateDTO dto : projectDTOs) {
            projects.add(initializeProject(dto));
        }

        projects = projectRepository.saveAll(projects);
        List<ProjectDTO.ProjectResponseDTO> responses = new ArrayList<>();

        for(ProjectEntity project : projects)
            responses.add(convertToResponseDTO(project));


        return responses;
    }

    @Override
    public ProjectDTO.ProjectResponseDTO updateProject(ProjectDTO.ProjectCreateDTO projectDTO, Long id) {
        ProjectEntity updatedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        updatedProject.setProjectName(projectDTO.getProjectName());
        updatedProject.setInitialDate(projectDTO.getInitialDate());
        updatedProject.updateCycle(processSimpleRelations(projectDTO,  updatedProject));

        return convertToResponseDTO(projectRepository.save(updatedProject));
    }

    @Override
    public String deleteProject(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        if ((project.getPersonalProjectInterestsList() != null && !project.getPersonalProjectInterestsList().isEmpty()) ||
                (isCycleWithDependencies(project.getCycleList()))) {
            throw new BusinessRuleException("ERR_PROJECT_CANT_BE_DELETED");
        }

        projectRepository.delete(project);
        return "PROJECT_SUCCESSFULLY_DELETED";
    }

    @Override
    public List<ProjectDTO.ProjectSimpleDTO> findAllProjects() {
        return projectRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity ->  modelMapper.map(entity, ProjectDTO.ProjectSimpleDTO.class))
                .toList();
    }

    @Override
    public ProjectDTO.ProjectResponseDTO findProjectById(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        return convertToResponseDTO(project);
    }

    public List<ProjectDTO.ProjectSimpleDTO> findAllProjectsByState(ProjectState state) {
        return projectRepository.findByState(state)
                .stream()
                .map(entity -> modelMapper.map(entity, ProjectDTO.ProjectSimpleDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO.ProjectBossCompetitionsDTO> findBossRoleCompetitionsByProjectIds(List<Long> projectIds) {
        List<ProjectDTO.ProjectBossCompetitionsDTO> result = new ArrayList<>();

        for (Long projectId : projectIds) {
            ProjectStructureInfo info = findProjectStructureInfo(projectId);

            RoleEntity bossRole = info.getProjectStructure().getProjectRolesList().stream()
                    .map(ProjectRolesEntity::getRole)
                    .filter(role -> role != null && role.isBoss())
                    .findFirst()
                    .orElseThrow(() -> new BusinessRuleException("ERR_PROJECT_NOT_BOSS_FOUND", projectId));

            List<RoleDTO.RoleCompetitionResponseDTO> technical = new ArrayList<>();
            List<RoleDTO.RoleCompetitionResponseDTO> nonTechnical = new ArrayList<>();

            for(RoleCompetitionEntity rc: bossRole.getRoleCompetitionList()){
                boolean isTechnical;
                if (rc.getCompetence() != null)
                    isTechnical = rc.getCompetence().getTechnical();
                else
                    throw new BusinessRuleException("ERR_PROJECT_ROLE_COMPETENCE_NULL");

                if(isTechnical)
                    technical.add(toRoleCompetitionResponseDTO(rc));
                else
                    nonTechnical.add(toRoleCompetitionResponseDTO(rc));
            }
            ProjectDTO.ProjectBossCompetitionsDTO dto = new ProjectDTO.ProjectBossCompetitionsDTO(info.getProject().getId(), info.getProject().getProjectName(), technical, nonTechnical);
            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO.ProjectNonBossRolesDTO> findNonBossRolesByProjectIds(List<Long> projectIds) {
        List<ProjectDTO.ProjectNonBossRolesDTO> result = new ArrayList<>();

        for (Long projectId : projectIds) {
            ProjectStructureInfo info = findProjectStructureInfo(projectId);//NOSONAR

            List<RoleDTO.RoleMinimalDTO> nonBossRoles = info.getProjectStructure().getProjectRolesList().stream()//NOSONAR
                    .map(ProjectRolesEntity::getRole)
                    .filter(role -> role != null && !role.isBoss())   // solo no jefe
                    .map(role -> modelMapper.map(role, RoleDTO.RoleMinimalDTO.class))
                    .collect(Collectors.toList());

            result.add(new ProjectDTO.ProjectNonBossRolesDTO(
                    info.getProject().getId(),
                    info.getProject().getProjectName(),
                    nonBossRoles
            ));
        }

        return result;
    }

    /**
     * Devuelve los roles asignados (agrupados por rol) de un proyecto para las
     * personas que NO son jefe. Se usa en la vista de "Finalizar equipo" para
     * listar a los miembros a evaluar.
     */
    @Transactional(readOnly = true)
    public List<AssignedRoleDTO> findNonBossAssignedRoles(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", projectId));

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", projectId);

        CycleEntity cycle = project.getCycleList().getFirst();
        Map<Long, AssignedRoleDTO> byRole = new LinkedHashMap<>();

        List<AssignedRoleEntity> assignedRoles = cycle.getAssignedRoleList();
        if (assignedRoles != null) {
            // No se filtra por estado ACTIVE: al finalizar se desactivan las asignaciones,
            // pero siguen representando al equipo (necesario para finalizar y cerrar).
            for (AssignedRoleEntity assignedRole : assignedRoles) {
                RoleEntity role = assignedRole.getRole();
                if (role != null && !role.isBoss() && assignedRole.getPerson() != null) {
                    AssignedRoleDTO dto = byRole.computeIfAbsent(role.getId(),
                            k -> new AssignedRoleDTO(modelMapper.map(role, RoleDTO.RoleMinimalDTO.class)));
                    dto.getPersons().add(modelMapper.map(assignedRole.getPerson(), PersonDTO.PersonMinimalDTO.class));
                }
            }
        }

        return new ArrayList<>(byRole.values());
    }

    /**
     * Devuelve el rol de jefe (isBoss = true) del equipo con la(s) persona(s)
     * asignada(s). Se usa en la vista de "Cerrar equipo" para evaluar al jefe.
     */
    @Transactional(readOnly = true)
    public AssignedRoleDTO findBossAssignedRole(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", projectId));

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", projectId);

        CycleEntity cycle = project.getCycleList().getFirst();
        AssignedRoleDTO dto = null;
        Set<Long> seenPersons = new HashSet<>();

        List<AssignedRoleEntity> assignedRoles = cycle.getAssignedRoleList();
        if (assignedRoles != null) {
            for (AssignedRoleEntity assignedRole : assignedRoles) {
                RoleEntity role = assignedRole.getRole();
                PersonEntity person = assignedRole.getPerson();
                if (role == null || person == null || !role.isBoss())
                    continue;
                if (dto == null)
                    dto = new AssignedRoleDTO(modelMapper.map(role, RoleDTO.RoleMinimalDTO.class));
                if (seenPersons.add(person.getId()))
                    dto.getPersons().add(modelMapper.map(person, PersonDTO.PersonMinimalDTO.class));
            }
        }

        return dto;
    }

    /**
     * Cierra un proyecto en estado FINALIZED: registra la evaluación del jefe,
     * asigna la evaluación del proyecto y cambia el estado a CLOSED.
     */
    @Transactional
    public ProjectDTO.ProjectResponseDTO closeProject(Long id, CloseProjectDTO dto) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        if (project.getState() != ProjectState.FINALIZED)
            throw new BusinessRuleException("ERR_PROJECT_CLOSE_INVALID_STATE");

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", id);

        CycleEntity cycle = project.getCycleList().getFirst();
        if (cycle == null)
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", id);

        // Evaluación del jefe -> se agrega a las evaluaciones del ciclo (sin borrar las de los miembros)
        if (cycle.getRoleEvaluationList() == null)
            cycle.setRoleEvaluationList(new ArrayList<>());
        cycle.getRoleEvaluationList().addAll(
                buildRolePersonEvaluations(List.of(dto.getBossEvaluation()), cycle));

        // Evaluación del proyecto
        RoleEvaluationEntity projectEvaluation = roleEvaluationRepository.findById(dto.getRoleEvaluation())
                .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_EVAL_NOT_FOUND", dto.getRoleEvaluation()));
        project.setRoleEvaluation(projectEvaluation);

        project.setStateToNext(); // FINALIZED -> CLOSED

        return convertToResponseDTO(projectRepository.save(project));
    }

    /**
     * Finaliza un proyecto en estado FORMED: registra la evaluación de cada
     * persona en su rol y cambia el estado a FINALIZED.
     */
    @Transactional
    public ProjectDTO.ProjectResponseDTO finalizeProject(Long id, List<RolePersonEvaluationDTO> evaluations) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        if (project.getState() != ProjectState.FORMED)
            throw new BusinessRuleException("ERR_PROJECT_FINALIZE_INVALID_STATE");

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", id);

        CycleEntity cycle = project.getCycleList().getFirst();
        if (cycle == null)
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", id);

        List<RolePersonEvalEntity> roleEvaluations = buildRolePersonEvaluations(evaluations, cycle);

        if (cycle.getRoleEvaluationList() == null)
            cycle.setRoleEvaluationList(new ArrayList<>());
        cycle.getRoleEvaluationList().clear();
        cycle.getRoleEvaluationList().addAll(roleEvaluations);
        cycle.deactivateAllAssignedRoles();

        project.setStateToNext(); // FORMED -> FINALIZED
        subtractWorkloadForActiveAssignments(cycle);

        return convertToResponseDTO(projectRepository.save(project));
    }

    private void subtractWorkloadForActiveAssignments(CycleEntity cycle) {
        List<AssignedRoleEntity> assignedRoles = cycle.getAssignedRoleList();
        if (assignedRoles == null || assignedRoles.isEmpty()) {
            return;
        }
        List<PersonEntity> personsToUpdate = new ArrayList<>();
        for (AssignedRoleEntity assignedRole : assignedRoles) {
            if (assignedRole.getStatus() == Status.ACTIVE) {
                PersonEntity person = assignedRole.getPerson();
                RoleEntity role = assignedRole.getRole();
                Float roleLoadValue = getRoleLoadValueForCycle(cycle, role);
                float newWorkload = person.getWorkload() - roleLoadValue;
                if (newWorkload < 0) newWorkload = 0; // por seguridad
                person.setWorkload(newWorkload);
                personsToUpdate.add(person);
            }
        }
        if (!personsToUpdate.isEmpty()) {
            personRepository.saveAll(personsToUpdate);
        }
    }

    private Float getRoleLoadValueForCycle(CycleEntity cycle, RoleEntity role) {
        ProjectStructureEntity projectStructure = cycle.getProjectStructure();
        if (projectStructure == null) {
            throw new BusinessRuleException("ERR_PROJECT_CYCLE_NO_STRUCTURE");
        }
        return projectStructure.getProjectRolesList().stream()
                .filter(pr -> pr.getRole().equals(role))
                .findFirst()
                .map(pr -> pr.getRoleLoad().getValue())
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_ROLE_NOT_FOUND_STRUCTURE"));
    }



    private List<RolePersonEvalEntity> buildRolePersonEvaluations(List<RolePersonEvaluationDTO> evaluations, CycleEntity cycle) {
        List<RolePersonEvalEntity> result = new ArrayList<>();
        if (evaluations == null)
            return result;

        for (RolePersonEvaluationDTO dto : evaluations) {
            PersonEntity person = personRepository.findById(dto.getPerson())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", dto.getPerson()));
            RoleEntity role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_NOT_FOUND", dto.getRole()));
            RoleEvaluationEntity roleEvaluation = roleEvaluationRepository.findById(dto.getRoleEvaluation())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_EVAL_NOT_FOUND", dto.getRoleEvaluation()));

            RolePersonEvalEntity entity = new RolePersonEvalEntity();
            entity.setCycles(cycle);
            entity.setPerson(person);
            entity.setRoles(role);
            entity.setRoleEvaluation(roleEvaluation);
            result.add(entity);
        }

        return result;
    }

    /**
     * Reporte de un equipo: datos del proyecto y los miembros que trabajaron en
     * él (persona, rol y evaluación), obtenidos de las evaluaciones del ciclo.
     */
    @Transactional(readOnly = true)
    public ProjectReportDTO getProjectReport(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", id));

        ProjectReportDTO dto = new ProjectReportDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setInitialDate(project.getInitialDate());
        dto.setEndDate(project.getEndDate());
        dto.setState(project.getState());

        List<ProjectReportDTO.MemberDTO> members = new ArrayList<>();
        if (project.getCycleList() != null && !project.getCycleList().isEmpty()) {
            CycleEntity cycle = project.getCycleList().getFirst();
            if (cycle.getRoleEvaluationList() != null) {
                for (RolePersonEvalEntity rpe : cycle.getRoleEvaluationList()) {
                    members.add(new ProjectReportDTO.MemberDTO(
                            rpe.getPerson() != null ? modelMapper.map(rpe.getPerson(), PersonDTO.PersonMinimalDTO.class) : null,
                            rpe.getRoles() != null ? modelMapper.map(rpe.getRoles(), RoleDTO.RoleMinimalDTO.class) : null,
                            rpe.getRoleEvaluation() != null ? rpe.getRoleEvaluation().getSignificance() : null
                    ));
                }
            }
        }
        dto.setMembers(members);
        return dto;
    }

    private ProjectEntity initializeProject(ProjectDTO.ProjectCreateDTO dto) {
        ProjectEntity project = modelMapper.map(dto, ProjectEntity.class);

        project.setRoleEvaluation(null);
        project.setCycleList(new ArrayList<>());
        project.getCycleList().add(new CycleEntity(project, processSimpleRelations(dto, project)));

        return project;
    }

    private void duplicatedNames(List<ProjectDTO.ProjectCreateDTO> projectDTOs){
        Set<String> namesInBatch = new HashSet<>();
        List<String> duplicateNamesInBatch = projectDTOs.stream().map(dto -> {
                    if(projectRepository.existsByProjectName(dto.getProjectName()))
                        throw new BusinessRuleException("ERR_PROJECT_NAME_ALREADY_EXISTS", dto.getProjectName());

                    return dto.getProjectName();
                }).filter(name -> !namesInBatch.add(name))
                .toList();

        if (!duplicateNamesInBatch.isEmpty()) {
            throw new BusinessRuleException("ERR_PROJECT_BATCH_DUPLICATE_NAMES");
        }
    }

    private ProjectStructureEntity processSimpleRelations(ProjectDTO.ProjectCreateDTO projectDTO, ProjectEntity project) {
        project.setClient(clientRepository.findById(projectDTO.getClient())
                .orElseThrow(() -> new ResourceNotFoundException("ERR_CLIENT_NOT_FOUND", projectDTO.getClient())));
        project.setProvince(countyRepository.findById(projectDTO.getProvince())
                .orElseThrow(()-> new ResourceNotFoundException("ERR_COUNTY_NOT_FOUND", projectDTO.getProvince())));

        return projectStructureRepository.findById(projectDTO.getProjectStructure())
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_STRUCTURE_NOT_FOUND", projectDTO.getProjectStructure()));
    }

    private boolean isCycleWithDependencies(List<CycleEntity> cycles) {
        for (CycleEntity cycle : cycles) {
            if((cycle.getAssignedRoleList() != null && !cycle.getAssignedRoleList().isEmpty()) ||
                    (cycle.getRoleEvaluationList() != null && !cycle.getRoleEvaluationList().isEmpty()))
                return true;
        }
        return false;
    }

    private ProjectDTO.ProjectResponseDTO convertToResponseDTO(ProjectEntity project) {
        ProjectDTO.ProjectResponseDTO dto = modelMapper.map(project, ProjectDTO.ProjectResponseDTO.class);

        dto.setClient(modelMapper.map(project.getClient(), ClientDTO.ClientResponseDTO.class));
        dto.setCounty(modelMapper.map(project.getProvince(), CountyDTO.CountyResponseDTO.class));
        dto.setProjectStructure(modelMapper.map(project.getCycleList().getFirst().getProjectStructure(), ProjectStructureDTO.ProjectStructureSimpleDTO.class));

        return dto;
    }

    private RoleDTO.RoleCompetitionResponseDTO toRoleCompetitionResponseDTO(RoleCompetitionEntity entity) {
        RoleDTO.RoleCompetitionResponseDTO dto = new RoleDTO.RoleCompetitionResponseDTO();
        dto.setId(entity.getId());
        dto.setCompetence(modelMapper.map(entity.getCompetence(), CompetenceDTO.CompetenceMinimalDTO.class));
        dto.setCompetenceImportance(modelMapper.map(entity.getCompetenceImportance(), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class));
        dto.setLevel(modelMapper.map(entity.getLevel(), LevelsDTO.LevelsResponseDTO.class));

        return dto;
    }

    private ProjectStructureInfo findProjectStructureInfo(Long projectId){
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", projectId));

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("ERR_PROJECT_NO_CYCLE_DEFINED", projectId);

        ProjectStructureEntity structure = project.getCycleList().getFirst().getProjectStructure();
        if (structure == null || structure.getProjectRolesList() == null)
            throw new BusinessRuleException("ERR_PROJECT_STRUCTURE_OR_ROLES_NOT_FOUND", projectId);

        return new ProjectStructureInfo(project, structure);
    }
}