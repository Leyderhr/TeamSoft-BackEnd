package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IProjectStructureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectStructureServiceImpl implements IProjectStructureService {

    private final IProjectStructureRepository projectStructureRepository;
    private final IRoleRepository roleRepository;
    private final IRoleLoadRepository roleLoadRepository;
    private final ICompetenceRepository competenceRepository;
    private final ICompetenceImportanceRepository competenceImportanceRepository;
    private final ILevelsRepository levelsRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ProjectStructureDTO.ProjectStructureResponseDTO saveProjectStructure(
            ProjectStructureDTO.ProjectStructureCreateDTO projectStructureDTO) {

        ProjectStructureEntity projectStructure = modelMapper.map(projectStructureDTO, ProjectStructureEntity.class);

        if(projectStructureDTO.getProjectRoles() != null) {
            projectStructure.setProjectRolesList(processProjectRoles(projectStructureDTO.getProjectRoles(), projectStructure));
        }
        return convertToResponseDTO(projectStructureRepository.save(projectStructure));
    }

    @Override
    @Transactional
    public ProjectStructureDTO.ProjectStructureResponseDTO updateProjectStructure(
            ProjectStructureDTO.ProjectStructureCreateDTO projectStructureDTO, Long id) {

        ProjectStructureEntity existingProjectStructure =
                projectStructureRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("ProjectStructure not found with ID: " + id));

        existingProjectStructure.setName(projectStructureDTO.getName());

        List<ProjectRolesEntity> validatedProjectRoles = null;
        if(projectStructureDTO.getProjectRoles() != null) {
            validatedProjectRoles = processProjectRoles(projectStructureDTO.getProjectRoles(), existingProjectStructure);
        }
        syncProjectRoles(existingProjectStructure, validatedProjectRoles);

        return convertToResponseDTO(projectStructureRepository.save(existingProjectStructure));
    }

    @Override
    @Transactional
    public String deleteProjectStructure(Long id) {
        ProjectStructureEntity projectStructure = projectStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectStructure not found with ID: " + id));

        if (projectStructure.getCycleList() != null && !projectStructure.getCycleList().isEmpty()) {
            throw new BusinessRuleException("Cannot delete project structure because it has associated cycles");
        }

        projectStructureRepository.deleteById(id);
        return "Project structure deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectStructureDTO.ProjectStructureSimpleDTO> findAllProjectStructure() {
        return projectStructureRepository.findAllByOrderByIdAsc().stream()
                .map(this::convertToSimpleDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectStructureDTO.ProjectStructureResponseDTO findProjectStructureById(Long id) {
        ProjectStructureEntity projectStructure = projectStructureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectStructure not found with ID: " + id));

        return convertToResponseDTO(projectStructure);
    }

    private List<ProjectRolesEntity> processProjectRoles(
            List<ProjectRoleDTO.ProjectRoleCreateDTO> projectRolesDTO,
            ProjectStructureEntity projectStructure) {

        if (projectRolesDTO == null || projectRolesDTO.isEmpty()) {
            return new ArrayList<>();
        }

        final boolean[] alreadyBoss = {false};

        return projectRolesDTO.stream().map(dto -> {
            RoleEntity role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + dto.getRole()));

            if(role.isBoss() && alreadyBoss[0]) {
                throw new BusinessRuleException("A role with 'boss' type already exists in the project structure");
            } else if (role.isBoss()) {
                alreadyBoss[0] = true;

                if(dto.getAmountWorkersRole() != 1) {
                    throw new BusinessRuleException("A role with 'boss' type must have exactly 1 worker");
                }
            }

            RoleLoadEntity roleLoad = roleLoadRepository.findById(dto.getRoleLoad())
                    .orElseThrow(() -> new ResourceNotFoundException("RoleLoad not found with ID: " + dto.getRoleLoad()));

            ProjectRolesEntity projectRole = new ProjectRolesEntity();
            projectRole.setAmountWorkersRole(dto.getAmountWorkersRole());
            projectRole.setProjectStructure(projectStructure);
            projectRole.setRole(role);
            projectRole.setRoleLoad(roleLoad);

            List<ProjectTechCompetenceEntity> techCompetences =
                    validateAndCreateTechCompetencesForRole(dto, projectRole);
            projectRole.setProjectTechCompetenceList(techCompetences);

            return projectRole;
        }).toList();
    }

    private void syncProjectRoles(ProjectStructureEntity projectStructure, List<ProjectRolesEntity> validatedProjectRoles) {
        if (validatedProjectRoles == null || validatedProjectRoles.isEmpty()) {
            projectStructure.getProjectRolesList().clear();
            return;
        }

        Map<Long, ProjectRolesEntity> existingMap = projectStructure.getProjectRolesList().stream()
                .collect(Collectors.toMap(pr -> pr.getRole().getId(), pr -> pr));

        List<ProjectRolesEntity> updatedList = new ArrayList<>();

        for (ProjectRolesEntity validatePR : validatedProjectRoles) {
            Long roleId = validatePR.getRole().getId();

            if (existingMap.containsKey(roleId)) {
                ProjectRolesEntity existing = existingMap.get(roleId);
                existing.setAmountWorkersRole(validatePR.getAmountWorkersRole());
                existing.setRoleLoad(validatePR.getRoleLoad());

                syncTechCompetencesForRole(existing, validatePR.getProjectTechCompetenceList());

                updatedList.add(existing);
            } else
                updatedList.add(validatePR);
        }

        projectStructure.getProjectRolesList().clear();
        projectStructure.getProjectRolesList().addAll(updatedList);
    }

    private List<ProjectTechCompetenceEntity> validateAndCreateTechCompetencesForRole(
            ProjectRoleDTO.ProjectRoleCreateDTO dto,
            ProjectRolesEntity projectRole) {

        if (dto.getTechCompetences() == null || dto.getTechCompetences().isEmpty()) {
            return new ArrayList<>();
        }

        return dto.getTechCompetences().stream().map(tcDto -> {
            CompetenceEntity competence = competenceRepository.findById(tcDto.getCompetenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competence not found with ID: " + tcDto.getCompetenceId()));
            CompetenceImportanceEntity importance = competenceImportanceRepository.findById(tcDto.getCompetenceImportanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("CompetenceImportance not found with ID: " + tcDto.getCompetenceImportanceId()));
            LevelsEntity level = levelsRepository.findById(tcDto.getLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Level not found with ID: " + tcDto.getLevelId()));

            ProjectTechCompetenceEntity ptc = new ProjectTechCompetenceEntity();
            ptc.setCompetence(competence);
            ptc.setCompetenceImportance(importance);
            ptc.setLevel(level);
            ptc.setProjectRoles(projectRole); // relación inversa
            return ptc;
        }).toList();
    }

    private void syncTechCompetencesForRole(ProjectRolesEntity existingRole,
                                            List<ProjectTechCompetenceEntity> newTechList) {
        if (newTechList == null || newTechList.isEmpty()) {
            existingRole.getProjectTechCompetenceList().clear();
            return;
        }

        // Mapa de competencias existentes, clave única: competenceId-importanceId-levelId
        Map<String, ProjectTechCompetenceEntity> existingMap = existingRole.getProjectTechCompetenceList()
                .stream()
                .collect(Collectors.toMap(
                        this::buildTechCompetenceKey,
                        ptc -> ptc
                ));

        List<ProjectTechCompetenceEntity> updatedList = new ArrayList<>();

        for (ProjectTechCompetenceEntity newPtc : newTechList) {
            String key = buildTechCompetenceKey(newPtc);
            if (existingMap.containsKey(key)) {
                // Se reutiliza la existente (ya tiene ID)
                updatedList.add(existingMap.get(key));
            } else {
                // Es una nueva competencia, se vincula al rol
                newPtc.setProjectRoles(existingRole);
                updatedList.add(newPtc);
            }
        }

        existingRole.getProjectTechCompetenceList().clear();
        existingRole.getProjectTechCompetenceList().addAll(updatedList);
    }

    private String buildTechCompetenceKey(ProjectTechCompetenceEntity ptc) {
        return ptc.getCompetence().getId() + "-"
                + ptc.getCompetenceImportance().getId() + "-"
                + ptc.getLevel().getId();
    }

    private ProjectStructureDTO.ProjectStructureResponseDTO convertToResponseDTO(ProjectStructureEntity entity) {
        ProjectStructureDTO.ProjectStructureResponseDTO responseDTO =
                modelMapper.map(entity, ProjectStructureDTO.ProjectStructureResponseDTO.class);

        if (entity.getProjectRolesList() != null) {
            responseDTO.setProjectRoles(entity.getProjectRolesList().stream()
                    .map(proR -> {
                        ProjectRoleDTO.ProjectRoleResponseDTO dto = new ProjectRoleDTO.ProjectRoleResponseDTO();

                        dto.setId(proR.getId());
                        dto.setAmountWorkersRole(proR.getAmountWorkersRole());
                        dto.setRole(modelMapper.map(proR.getRole(), RoleDTO.RoleMinimalDTO.class));
                        dto.setRoleLoad(modelMapper.map(proR.getRoleLoad(), RoleLoadDTO.RoleLoadResponseDTO.class));

                        if (proR.getProjectTechCompetenceList() != null) {
                            dto.setTechCompetences(proR.getProjectTechCompetenceList().stream()
                                    .map(this::convertToTechCompetenceResponseDTO)
                                    .toList());
                        }

                        return dto;
                    })
                    .toList());
        }

        return responseDTO;
    }

    private ProjectStructureDTO.ProjectStructureSimpleDTO convertToSimpleDTO(ProjectStructureEntity entity) {
        ProjectStructureDTO.ProjectStructureSimpleDTO dto =
                new ProjectStructureDTO.ProjectStructureSimpleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    private ProjectTechCompetenceDTO.ProjectTechCompetenceResponseDTO convertToTechCompetenceResponseDTO(
            ProjectTechCompetenceEntity ptc) {
        ProjectTechCompetenceDTO.ProjectTechCompetenceResponseDTO dto =
                new ProjectTechCompetenceDTO.ProjectTechCompetenceResponseDTO();
        dto.setId(ptc.getId());
        dto.setCompetence(modelMapper.map(ptc.getCompetence(), CompetenceDTO.CompetenceMinimalDTO.class));
        dto.setImportance(modelMapper.map(ptc.getCompetenceImportance(), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class)      );
        dto.setLevel(modelMapper.map(ptc.getLevel(), LevelsDTO.LevelsResponseDTO.class));
        return dto;
    }
}
