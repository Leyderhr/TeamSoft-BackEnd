package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
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
    private final ModelMapper modelMapper;


    @Override
    public List<ProjectDTO.ProjectResponseDTO> saveProjects(List<ProjectDTO.ProjectCreateDTO> projectDTOs) {
        duplicatedNames(projectDTOs);
        List<ProjectEntity> projects = new ArrayList<>();

        for(ProjectDTO.ProjectCreateDTO dto : projectDTOs) {
            projects.add(initializeProject(dto));
        }

        List<ProjectDTO.ProjectResponseDTO> responses = new ArrayList<>();
        for(ProjectEntity project : projects) {
            responses.add(convertToResponseDTO(projectRepository.save(project)));
        }

        return responses;
    }

    @Override
    public ProjectDTO.ProjectResponseDTO updateProject(ProjectDTO.ProjectCreateDTO projectDTO, Long id) {
        ProjectEntity updatedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        updatedProject.setProjectName(projectDTO.getProjectName());
        updatedProject.setInitialDate(projectDTO.getInitialDate());
        updatedProject.updateCycle(processSimpleRelations(projectDTO,  updatedProject));

        return convertToResponseDTO(projectRepository.save(updatedProject));
    }

    public ProjectDTO.ProjectResponseDTO closeProject(Long id){
        ProjectEntity updatedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        return convertToResponseDTO(projectRepository.save(updatedProject));
    }

    @Override
    public String deleteProject(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        if ((project.getPersonalProjectInterestsList() != null && !project.getPersonalProjectInterestsList().isEmpty()) ||
                (isCycleWithDependencies(project.getCycleList()))) {
            throw new BusinessRuleException("Cannot delete project because it has associated datas");
        }

        projectRepository.delete(project);
        return "Person deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

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
                    .orElseThrow(() -> new BusinessRuleException("No boss role found in project structure for project ID: " + projectId));

            List<RoleDTO.RoleCompetitionResponseDTO> technical = new ArrayList<>();
            List<RoleDTO.RoleCompetitionResponseDTO> nonTechnical = new ArrayList<>();

            for(RoleCompetitionEntity rc: bossRole.getRoleCompetitionList()){
                boolean isTechnical;
                if (rc.getCompetence() != null)
                    isTechnical = rc.getCompetence().getTechnical();
                else
                    throw new BusinessRuleException("Competence is null in role competition");

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
                        throw new BusinessRuleException("Project name already exists: " + dto.getProjectName());

                    return dto.getProjectName();
                }).filter(name -> !namesInBatch.add(name))
                .toList();

        if (!duplicateNamesInBatch.isEmpty()) {
            throw new BusinessRuleException("Duplicate project names in the same request: " + duplicateNamesInBatch);
        }
    }

    private ProjectStructureEntity processSimpleRelations(ProjectDTO.ProjectCreateDTO projectDTO, ProjectEntity project) {
        project.setClient(clientRepository.findById(projectDTO.getClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + projectDTO.getClient())));
        project.setProvince(countyRepository.findById(projectDTO.getProvince())
                .orElseThrow(()-> new ResourceNotFoundException("Province not found with id: " + projectDTO.getProvince())));

        return projectStructureRepository.findById(projectDTO.getProjectStructure())
                .orElseThrow(() -> new ResourceNotFoundException("Project structure not found with id: " + projectDTO.getProjectStructure()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getCycleList() == null || project.getCycleList().isEmpty())
            throw new BusinessRuleException("Project with ID " + projectId + " has no cycles defined");

        ProjectStructureEntity structure = project.getCycleList().getFirst().getProjectStructure();
        if (structure == null || structure.getProjectRolesList() == null)
            throw new BusinessRuleException("Project structure or roles not found for project ID: " + projectId);

        return new ProjectStructureInfo(project, structure);
    }
}