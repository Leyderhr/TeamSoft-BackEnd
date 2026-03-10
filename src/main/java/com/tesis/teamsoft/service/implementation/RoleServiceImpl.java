package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository roleRepository;
    private final ICompetenceRepository competenceRepository;
    private final ICompetenceImportanceRepository competenceImportanceRepository;
    private final ILevelsRepository levelsRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public RoleDTO.RoleResponseDTO saveRole(RoleDTO.RoleCreateDTO roleDTO) {
        try {
            RoleEntity role = modelMapper.map(roleDTO, RoleEntity.class);

            if (roleDTO.getRoleCompetitions() != null) {
                role.setRoleCompetitionList(processRoleCompetitions(roleDTO.getRoleCompetitions(), role));
            }

            if (roleDTO.getIncompatibleRoleIds() != null) {
                role.setIncompatibleRoles(processIncompatibleRoles(roleDTO.getIncompatibleRoleIds(), role));
            }

            return convertToResponseDTO(roleRepository.save(role));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Role name already exists");
        } catch (Exception e) {
            throw new RuntimeException("Error saving role: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public RoleDTO.RoleResponseDTO updateRole(RoleDTO.RoleCreateDTO roleDTO, Long id) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        try {
            // Actualizar campos básicos
            existingRole.setRoleName(roleDTO.getRoleName());
            existingRole.setRoleDesc(roleDTO.getRoleDesc());
            existingRole.setImpact(roleDTO.getImpact());
            existingRole.setBoss(roleDTO.getIsBoss());

            List<RoleCompetitionEntity> validatedCompetitions = null;
            if (roleDTO.getRoleCompetitions() != null) {
                validatedCompetitions = processRoleCompetitions(roleDTO.getRoleCompetitions(), existingRole);
            }

            syncRoleCompetitions(existingRole, validatedCompetitions);

            if (roleDTO.getIncompatibleRoleIds() != null) {
                existingRole.setIncompatibleRoles(processIncompatibleRoles(roleDTO.getIncompatibleRoleIds(), existingRole));
            } else {
                existingRole.setIncompatibleRoles(new ArrayList<>());
            }

            return convertToResponseDTO(roleRepository.save(existingRole));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Role name already exists");
        } catch (Exception e) {
            throw new RuntimeException("Error updating role: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String deleteRole(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        if (!role.getAssignedRoleList().isEmpty() ||
                !role.getRoleExperienceList().isEmpty() || !role.getPersonalInterestsList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role because it has associated relations");
        }

        roleRepository.deleteById(id);
        return "Role deleted successfully";
    }

    @Override
    public List<RoleDTO.RoleResponseDTO> findAllRole() {
        try {
            return roleRepository.findAll().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all roles: " + e.getMessage());
        }
    }

    @Override
    public List<RoleDTO.RoleResponseDTO> findAllByOrderByIdAsc() {
        try {
            return roleRepository.findAllByOrderByIdAsc().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all roles: " + e.getMessage());
        }
    }

    @Override
    public RoleDTO.RoleResponseDTO findRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
        return convertToResponseDTO(role);
    }

    private List<RoleCompetitionEntity> processRoleCompetitions(
            List<RoleDTO.RoleCompetitionCreateDTO> competitionsDTO, RoleEntity role) {

        if (competitionsDTO == null || competitionsDTO.isEmpty()) {
            return new ArrayList<>();
        }

        return competitionsDTO.stream().map(dto -> {
            // Validar que existen las entidades (solo por ID)
            CompetenceEntity competence = competenceRepository.findById(dto.getCompetenceId())
                    .orElseThrow(() -> new RuntimeException("Competence not found with ID: " + dto.getCompetenceId()));

            CompetenceImportanceEntity importance = competenceImportanceRepository.findById(dto.getCompetenceImportanceId())
                    .orElseThrow(() -> new RuntimeException("Competence Importance not found with ID: " + dto.getCompetenceImportanceId()));

            LevelsEntity level = levelsRepository.findById(dto.getLevelsId())
                    .orElseThrow(() -> new RuntimeException("Levels not found with ID: " + dto.getLevelsId()));

            RoleCompetitionEntity rc = new RoleCompetitionEntity();
            rc.setCompetence(competence);
            rc.setCompetenceImportance(importance);
            rc.setLevel(level);
            rc.setRole(role);
            return rc;
        }).collect(Collectors.toList());
    }

    private void syncRoleCompetitions(RoleEntity role, List<RoleCompetitionEntity> validatedCompetitions) {
        // Si no hay competencias validadas, limpiar la lista
        if (validatedCompetitions == null || validatedCompetitions.isEmpty()) {
            role.getRoleCompetitionList().clear();
            return;
        }

        // Mapa de competencias existentes por competenceId
        Map<Long, RoleCompetitionEntity> existingMap = role.getRoleCompetitionList().stream()
                .collect(Collectors.toMap(rc -> rc.getCompetence().getId(), rc -> rc));

        // Lista final para el rol
        List<RoleCompetitionEntity> finalList = new ArrayList<>();

        for (RoleCompetitionEntity validatedRc : validatedCompetitions) {
            Long competenceId = validatedRc.getCompetence().getId();

            if (existingMap.containsKey(competenceId)) {
                RoleCompetitionEntity existing = existingMap.get(competenceId);
                existing.setCompetenceImportance(validatedRc.getCompetenceImportance());
                existing.setLevel(validatedRc.getLevel());
                finalList.add(existing);
            } else {
                finalList.add(validatedRc);
            }
        }
        role.getRoleCompetitionList().clear();
        role.getRoleCompetitionList().addAll(finalList);
    }

    private List<RoleEntity> processIncompatibleRoles(List<Long> incompatibleRoleIds, RoleEntity currentRole) {
        Set<Long> processedIds = new HashSet<>();

        return incompatibleRoleIds.stream()
                .filter(roleId -> {
                    if (roleId.equals(currentRole.getId())) {
                        throw new IllegalArgumentException("Role cannot be incompatible with itself");
                    }
                    return processedIds.add(roleId);
                })
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Incompatible role not found with ID: " + roleId)))
                .collect(Collectors.toList());
    }

    private RoleDTO.RoleResponseDTO convertToResponseDTO(RoleEntity role) {
        RoleDTO.RoleResponseDTO responseDTO = modelMapper.map(role, RoleDTO.RoleResponseDTO.class);
        responseDTO.setIsBoss(role.isBoss());

        // Convertir RoleCompetitions
        if (role.getRoleCompetitionList() != null) {
            responseDTO.setRoleCompetitions(role.getRoleCompetitionList().stream()
                    .map(rc -> {
                        RoleDTO.RoleCompetitionResponseDTO dto = new RoleDTO.RoleCompetitionResponseDTO();
                        dto.setId(rc.getId());
                        dto.setCompetence(modelMapper.map(rc.getCompetence(), CompetenceDTO.CompetenceMinimalDTO.class));
                        dto.setCompetenceImportance(modelMapper.map(rc.getCompetenceImportance(), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class));
                        dto.setLevel(modelMapper.map(rc.getLevel(), LevelsDTO.LevelsResponseDTO.class));
                        return dto;
                    })
                    .collect(Collectors.toList()));
        }

        // Convertir IncompatibleRoles
        if (role.getIncompatibleRoles() != null) {
            responseDTO.setIncompatibleRoles(role.getIncompatibleRoles().stream()
                    .map(ir -> {
                        RoleDTO.RoleMinimalDTO dto = new RoleDTO.RoleMinimalDTO();
                        dto.setId(ir.getId());
                        dto.setRoleName(ir.getRoleName());
                        return dto;
                    })
                    .collect(Collectors.toList()));
        }

        return responseDTO;
    }
}