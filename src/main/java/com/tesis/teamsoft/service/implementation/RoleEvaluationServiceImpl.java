package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.RoleEvaluationEntity;
import com.tesis.teamsoft.persistence.repository.IRoleEvaluationRepository;
import com.tesis.teamsoft.presentation.dto.RoleEvaluationDTO;
import com.tesis.teamsoft.service.interfaces.IRoleEvaluationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleEvaluationServiceImpl implements IRoleEvaluationService {

    private final IRoleEvaluationRepository roleEvaluationRepository;
    private final ModelMapper modelMapper;


    @Override
    public RoleEvaluationDTO.RoleEvaluationResponseDTO saveRoleEvaluation(RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO) {
        try {
            RoleEvaluationEntity savedRoleEvaluation = modelMapper.map(roleEvaluationDTO, RoleEvaluationEntity.class);
            return modelMapper.map(roleEvaluationRepository.save(savedRoleEvaluation), RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving role evaluation: " + e.getMessage());
        }
    }

    @Override
    public RoleEvaluationDTO.RoleEvaluationResponseDTO updateRoleEvaluation(RoleEvaluationDTO.RoleEvaluationCreateDTO roleEvaluationDTO, Long id) {

        if (!roleEvaluationRepository.existsById(id)) {
            throw new RuntimeException("Role evaluation not found with ID: " + id);
        }

        try {
            RoleEvaluationEntity updatedRoleEvaluation = modelMapper.map(roleEvaluationDTO, RoleEvaluationEntity.class);
            updatedRoleEvaluation.setId(id);
            return modelMapper.map(roleEvaluationRepository.save(updatedRoleEvaluation), RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating role evaluation: " + e.getMessage());
        }
    }

    @Override
    public String deleteRoleEvaluation(Long id) {
        RoleEvaluationEntity roleEvaluation = roleEvaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role evaluation not found with ID: " + id));

        // Verificar si tiene relaciones antes de eliminar
        if ((roleEvaluation.getProjectsList() != null && !roleEvaluation.getProjectsList().isEmpty()) ||
                (roleEvaluation.getRoleEvaluationList() != null && !roleEvaluation.getRoleEvaluationList().isEmpty())) {
            throw new IllegalArgumentException("Cannot delete role evaluation because it has associated relations");
        }

        roleEvaluationRepository.deleteById(id);
        return "Role evaluation deleted successfully";
    }

    @Override
    public List<RoleEvaluationDTO.RoleEvaluationResponseDTO> findAllRoleEvaluation() {
        try {
            return roleEvaluationRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RoleEvaluationDTO.RoleEvaluationResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all role evaluations: " + e.getMessage());
        }
    }

    @Override
    public List<RoleEvaluationDTO.RoleEvaluationResponseDTO> findAllByOrderByIdAsc() {
        try {
            return roleEvaluationRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RoleEvaluationDTO.RoleEvaluationResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all role evaluations: " + e.getMessage());
        }
    }

    @Override
    public RoleEvaluationDTO.RoleEvaluationResponseDTO findRoleEvaluationById(Long id) {
        RoleEvaluationEntity roleEvaluation = roleEvaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role evaluation not found with ID: " + id));

        return modelMapper.map(roleEvaluation, RoleEvaluationDTO.RoleEvaluationResponseDTO.class);
    }
}