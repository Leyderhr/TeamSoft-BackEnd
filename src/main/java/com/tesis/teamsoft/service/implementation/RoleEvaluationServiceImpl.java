package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.RoleEvaluationEntity;
import com.tesis.teamsoft.persistence.repository.IRoleEvaluationRepository;
import com.tesis.teamsoft.presentation.dto.RoleEvaluationDTO;
import com.tesis.teamsoft.service.interfaces.IRoleEvaluationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleEvaluationServiceImpl implements IRoleEvaluationService {

    private final IRoleEvaluationRepository roleEvaluationRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public RoleEvaluationDTO.RoleEvaluationResponseDTO saveRoleEvaluation(RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO) {
        RoleEvaluationEntity savedRoleEvaluation = modelMapper.map(roleEvaluationDTO, RoleEvaluationEntity.class);
        return modelMapper.map(roleEvaluationRepository.save(savedRoleEvaluation), RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
    }

    @Override
    @Transactional
    public RoleEvaluationDTO.RoleEvaluationResponseDTO updateRoleEvaluation(RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO, Long id) {
        if (!roleEvaluationRepository.existsById(id))
            throw new ResourceNotFoundException("Role evaluation not found with ID: " + id);

        RoleEvaluationEntity updatedRoleEvaluation = modelMapper.map(roleEvaluationDTO, RoleEvaluationEntity.class);
        updatedRoleEvaluation.setId(id);
        return modelMapper.map(roleEvaluationRepository.save(updatedRoleEvaluation), RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteRoleEvaluation(Long id) {
        RoleEvaluationEntity roleEvaluation = roleEvaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role evaluation not found with ID: " + id));

        if ((roleEvaluation.getProjectsList() != null && !roleEvaluation.getProjectsList().isEmpty()) ||
                (roleEvaluation.getRoleEvaluationList() != null && !roleEvaluation.getRoleEvaluationList().isEmpty())) {
            throw new BusinessRuleException("Cannot delete role evaluation because it has associated relations");
        }

        roleEvaluationRepository.deleteById(id);
        return "Role evaluation deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEvaluationDTO.RoleEvaluationResponseDTO> findAllRoleEvaluation() {
        return roleEvaluationRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, RoleEvaluationDTO.RoleEvaluationResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEvaluationDTO.RoleEvaluationResponseDTO> findAllByOrderByIdAsc() {
        return roleEvaluationRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, RoleEvaluationDTO.RoleEvaluationResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleEvaluationDTO.RoleEvaluationResponseDTO findRoleEvaluationById(Long id) {
        RoleEvaluationEntity roleEvaluation = roleEvaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role evaluation not found with ID: " + id));

        return modelMapper.map(roleEvaluation, RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
    }
}