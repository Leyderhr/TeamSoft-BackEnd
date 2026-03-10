package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.ClientDTO;
import com.tesis.teamsoft.presentation.dto.CountyDTO;
import com.tesis.teamsoft.presentation.dto.ProjectDTO;
import com.tesis.teamsoft.presentation.dto.ProjectStructureDTO;
import com.tesis.teamsoft.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
        try{
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
        }catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("Error updating project: Data integrity violation");
        }catch (IllegalArgumentException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving project: " + e.getMessage());
        }
    }

    @Override
    public ProjectDTO.ProjectResponseDTO updateProject(ProjectDTO.ProjectCreateDTO projectDTO, Long id) {
        List<ProjectEntity> allProjects = projectRepository.findAll();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        ProjectEntity updatedProject = projectMap.get(id);
        if (updatedProject == null) {
            throw new RuntimeException("Project not found with ID: " + id);
        }

        try{
            updatedProject.setProjectName(projectDTO.getProjectName());
            updatedProject.setInitialDate(projectDTO.getInitialDate());
            updatedProject.updateCycle(processSimpleRelations(projectDTO,  updatedProject));

            return convertToResponseDTO(projectRepository.save(updatedProject));
        }catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("Error updating project: Data integrity violation");
        }catch (IllegalArgumentException e){
            throw e;
        }catch (RuntimeException e){
            throw new RuntimeException("Error updating project: " + e.getMessage());
        }
    }

    public ProjectDTO.ProjectResponseDTO closeProject(Long id){
        List<ProjectEntity> allProjects = projectRepository.findByCloseFalse();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        ProjectEntity updatedProject = projectMap.get(id);
        if (updatedProject == null) {
            throw new RuntimeException("Project not found with ID: " + id);
        }

        try{
            updatedProject.setClose(true);
            return convertToResponseDTO(projectRepository.save(updatedProject));
        }catch (RuntimeException e){
            throw new RuntimeException("Error updating project: " + e.getMessage());
        }
    }

    @Override
    public String deleteProject(Long id) {
        List<ProjectEntity> allProjects = projectRepository.findAll();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        ProjectEntity project = projectMap.get(id);
        if (project == null) {
            throw new RuntimeException("Project not found with ID: " + id);
        }

        if ((project.getPersonalProjectInterestsList() != null && !project.getPersonalProjectInterestsList().isEmpty()) ||
                (isCycleWithDependencies(project.getCycleList()))) {
            throw new IllegalArgumentException("Cannot delete project because it has associated datas");
        }

        projectRepository.delete(project);
        return "Person deleted successfully";
    }

    @Override
    public List<ProjectDTO.ProjectSimpleDTO> findAllProjects() {
        try{
            return projectRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity ->  modelMapper.map(entity, ProjectDTO.ProjectSimpleDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all projects: " + e.getMessage());
        }
    }

    @Override
    public ProjectDTO.ProjectResponseDTO findProjectById(Long id) {

        List<ProjectEntity> allProjects = projectRepository.findAll();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        ProjectEntity project = projectMap.get(id);
        if (project == null) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        return convertToResponseDTO(project);
    }

    private ProjectEntity initializeProject(ProjectDTO.ProjectCreateDTO dto) {
        ProjectEntity project = modelMapper.map(dto, ProjectEntity.class);

        project.setClose(false);
        project.setFinalize(false);
        project.setRoleEvaluation(null);
        project.setCycleList(new ArrayList<>());
        project.getCycleList().add(new CycleEntity(project, processSimpleRelations(dto, project)));

        return project;
    }

    private void duplicatedNames(List<ProjectDTO.ProjectCreateDTO> projectDTOs){
        Set<String> namesInBatch = new HashSet<>();
        List<String> duplicateNamesInBatch = projectDTOs.stream().map(dto -> {
                    if(projectRepository.existsByProjectName(dto.getProjectName()))
                        throw new IllegalArgumentException("Project name already exists: " + dto.getProjectName());

                    return dto.getProjectName();
                }).filter(name -> !namesInBatch.add(name))
                .collect(Collectors.toList());

        if (!duplicateNamesInBatch.isEmpty()) {
            throw new IllegalArgumentException("Duplicate project names in the same request: " + duplicateNamesInBatch);
        }
    }

    private ProjectStructureEntity processSimpleRelations(ProjectDTO.ProjectCreateDTO projectDTO, ProjectEntity project) {
        project.setClient(clientRepository.findById(projectDTO.getClient())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + projectDTO.getClient())));
        project.setProvince(countyRepository.findById(projectDTO.getProvince())
                .orElseThrow(()-> new RuntimeException("Province not found with id: " + projectDTO.getProvince())));

        return projectStructureRepository.findById(projectDTO.getProjectStructure())
                .orElseThrow(() -> new RuntimeException("Project structure not found with id: " + projectDTO.getProjectStructure()));
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
}