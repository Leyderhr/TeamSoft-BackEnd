package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.RoleLoadEntity;
import com.tesis.teamsoft.persistence.repository.IRoleLoadRepository;
import com.tesis.teamsoft.presentation.dto.RoleLoadDTO;
import com.tesis.teamsoft.service.interfaces.IRoleLoadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleLoadServiceImpl implements IRoleLoadService {

    private final IRoleLoadRepository roleLoadRepository;
    private final ModelMapper modelMapper;


    @Override
    public RoleLoadDTO.RoleLoadResponseDTO saveRoleLoad(RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO) {
        try {
            RoleLoadEntity savedRoleLoad = modelMapper.map(roleLoadDTO, RoleLoadEntity.class);
            return modelMapper.map(roleLoadRepository.save(savedRoleLoad), RoleLoadDTO.RoleLoadResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving role load: " + e.getMessage());
        }
    }

    @Override
    public RoleLoadDTO.RoleLoadResponseDTO updateRoleLoad(RoleLoadDTO.RoleLoadCreateDTO roleLoadDTO, Long id) {

        if (!roleLoadRepository.existsById(id)) {
            throw new RuntimeException("Role load not found with ID: " + id);
        }

        try {
            RoleLoadEntity updatedRoleLoad = modelMapper.map(roleLoadDTO, RoleLoadEntity.class);
            updatedRoleLoad.setId(id);
            roleLoadRepository.save(updatedRoleLoad);
            return modelMapper.map(updatedRoleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating role load: " + e.getMessage());
        }
    }

    @Override
    public String deleteRoleLoad(Long id) {
        RoleLoadEntity roleLoad = roleLoadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role load not found with ID: " + id));

        // Verificar si tiene ProjectRolesEntity asociados antes de eliminar
        if (roleLoad.getProjectRolesList() != null && !roleLoad.getProjectRolesList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role load because it has associated project roles");
        }

        roleLoadRepository.deleteById(id);
        return "Role load deleted successfully";
    }

    @Override
    public List<RoleLoadDTO.RoleLoadResponseDTO> findAllRoleLoad() {
        try {
            return roleLoadRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RoleLoadDTO.RoleLoadResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all role loads: " + e.getMessage());
        }
    }

    @Override
    public List<RoleLoadDTO.RoleLoadResponseDTO> findAllByOrderByIdAsc() {
        try {
            return roleLoadRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RoleLoadDTO.RoleLoadResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all role loads: " + e.getMessage());
        }
    }

    @Override
    public RoleLoadDTO.RoleLoadResponseDTO findRoleLoadById(Long id) {
        RoleLoadEntity roleLoad = roleLoadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role load not found with ID: " + id));

        return modelMapper.map(roleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
    }
}