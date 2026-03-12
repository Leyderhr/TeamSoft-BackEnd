package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.RoleLoadEntity;
import com.tesis.teamsoft.persistence.repository.IRoleLoadRepository;
import com.tesis.teamsoft.presentation.dto.RoleLoadDTO;
import com.tesis.teamsoft.service.interfaces.IRoleLoadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        if (!roleLoadRepository.existsById(id))
            throw new ResourceNotFoundException("Role load not found with ID: " + id);

        RoleLoadEntity updatedRoleLoad = modelMapper.map(roleLoadDTO, RoleLoadEntity.class);
        updatedRoleLoad.setId(id);
        roleLoadRepository.save(updatedRoleLoad);
        return modelMapper.map(updatedRoleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteRoleLoad(Long id) {
        RoleLoadEntity roleLoad = roleLoadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role load not found with ID: " + id));

        if (roleLoad.getProjectRolesList() != null && !roleLoad.getProjectRolesList().isEmpty())
            throw new BusinessRuleException("Cannot delete role load because it has associated project roles");

        roleLoadRepository.deleteById(id);
        return "Role load deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("Role load not found with ID: " + id));

        return modelMapper.map(roleLoad, RoleLoadDTO.RoleLoadResponseDTO.class);
    }
}