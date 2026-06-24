package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import com.tesis.teamsoft.persistence.repository.IRoleLoadRepository;
import com.tesis.teamsoft.presentation.dto.RoleLoadDTO;
import com.tesis.teamsoft.service.interfaces.IRoleLoadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoleLoadServiceImpl implements IRoleLoadService {

    private final IRoleLoadRepository roleLoadRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public RoleLoadDTO.RoleLoadResponseDTO saveRoleLoad(RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO) {
        RoleLoadEntity savedRoleLoad = modelMapper.map(roleLoadDTO, RoleLoadEntity.class);
        return modelMapper.map(roleLoadRepository.save(savedRoleLoad), RoleLoadDTO.RoleLoadResponseDTO.class);
    }

    @Override
    @Transactional
    public RoleLoadDTO.RoleLoadResponseDTO updateRoleLoad(RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO, Long id) {
        RoleLoadEntity existing = roleLoadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_LOAD_NOT_FOUND", id));

        if (existing.getValue() != roleLoadDTO.getValue()) {
            validateRoleLoadNotInActiveProjects(existing);}

        RoleLoadEntity updatedRoleLoad = modelMapper.map(roleLoadDTO, RoleLoadEntity.class);
        updatedRoleLoad.setId(id);
        roleLoadRepository.save(updatedRoleLoad);
        return modelMapper.map(updatedRoleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteRoleLoad(Long id) {
        RoleLoadEntity roleLoad = roleLoadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_LOAD_NOT_FOUND", id));

        if (roleLoad.getProjectRolesList() != null && !roleLoad.getProjectRolesList().isEmpty())
            throw new BusinessRuleException("ERR_ROLE_LOAD_CANT_BE_DELETED");

        roleLoadRepository.deleteById(id);
        return "ROLE_LOAD_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleLoadDTO.RoleLoadResponseDTO> findAllRoleLoad() {
        return roleLoadRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, RoleLoadDTO.RoleLoadResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleLoadDTO.RoleLoadResponseDTO> findAllByOrderByIdAsc() {
        return roleLoadRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, RoleLoadDTO.RoleLoadResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleLoadDTO.RoleLoadResponseDTO findRoleLoadById(Long id) {
        RoleLoadEntity roleLoad = roleLoadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_LOAD_NOT_FOUND", id));

        return modelMapper.map(roleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
    }

    private void validateRoleLoadNotInActiveProjects(RoleLoadEntity roleLoad) {
        List<ProjectRolesEntity> projectRolesList = roleLoad.getProjectRolesList();
        if (projectRolesList == null || projectRolesList.isEmpty()) {
            return;
        }

        Optional<ProjectEntity> offendingProject = projectRolesList.stream()
                .map(ProjectRolesEntity::getProjectStructure) // 1. Obtener la estructura del proyecto a partir de cada ProjectRoles
                .filter(Objects::nonNull)
                .flatMap(structure -> Stream.ofNullable(structure.getCycleList())// 2. Obtener los ciclos asociados a esa estructura (si existen)
                        .flatMap(List::stream))
                .filter(Objects::nonNull)                 // 3. Obtener el proyecto de cada ciclo
                .map(CycleEntity::getProject)
                .filter(Objects::nonNull)
                .filter(project -> {                 // 4. Quedarnos solo con aquellos cuyo estado sea FORMED o FINALIZED
                    ProjectState state = project.getState();
                    return state == ProjectState.FORMED || state == ProjectState.FINALIZED;
                })
                .findFirst();                 // 5. Tomar el primero que cumpla (si existe)
        // Si se encontró algún proyecto infractor, lanzar excepción con detalles
        offendingProject.ifPresent(project -> {
            throw new BusinessRuleException("ERR_ROLE_LOAD_CANT_BE_UPDATED", project.getId());
        });
    }
}