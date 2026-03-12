package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
        RoleEntity role = modelMapper.map(roleDTO, RoleEntity.class);

        if (roleDTO.getRoleCompetitions() != null)
            role.setRoleCompetitionList(processRoleCompetitions(roleDTO.getRoleCompetitions(), role));

        if (roleDTO.getIncompatibleRoleIds() != null)
            role.setIncompatibleRoles(processIncompatibleRoles(roleDTO.getIncompatibleRoleIds(), role));


        return convertToResponseDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleDTO.RoleResponseDTO updateRole(RoleDTO.RoleCreateDTO roleDTO, Long id) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

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
    }

    @Override
    @Transactional
    public String deleteRole(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if((role.getAssignedRoleList() != null && !role.getAssignedRoleList().isEmpty()) ||
                (role.getRoleExperienceList() != null && !role.getRoleExperienceList().isEmpty()) ||
                (role.getPersonalInterestsList() != null && !role.getPersonalInterestsList().isEmpty()))
            throw new BusinessRuleException("Cannot delete role because it has associated relations");

        roleRepository.deleteById(id);
        return "Role deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO.RoleResponseDTO> findAllRole() {
        return roleRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO.RoleResponseDTO> findAllByOrderByIdAsc() {
        return roleRepository.findAllByOrderByIdAsc().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO.RoleResponseDTO findRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
        return convertToResponseDTO(role);
    }

    private List<RoleCompetitionEntity> processRoleCompetitions(List<RoleDTO.RoleCompetitionCreateDTO> competitionsDTO, RoleEntity role) {
        if (competitionsDTO == null || competitionsDTO.isEmpty())
            return new ArrayList<>();

        return competitionsDTO.stream().map(dto -> {
            // Validar que existen las entidades (solo por ID)
            CompetenceEntity competence = competenceRepository.findById(dto.getCompetenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competence not found with ID: " + dto.getCompetenceId()));

            CompetenceImportanceEntity importance = competenceImportanceRepository.findById(dto.getCompetenceImportanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competence Importance not found with ID: " + dto.getCompetenceImportanceId()));

            LevelsEntity level = levelsRepository.findById(dto.getLevelsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Levels not found with ID: " + dto.getLevelsId()));

            RoleCompetitionEntity rc = new RoleCompetitionEntity();
            rc.setCompetence(competence);
            rc.setCompetenceImportance(importance);
            rc.setLevel(level);
            rc.setRole(role);
            return rc;
        }).toList();
    }

    private void syncRoleCompetitions(RoleEntity role, List<RoleCompetitionEntity> validatedCompetitions) {
        if (validatedCompetitions == null || validatedCompetitions.isEmpty()) {
            role.getRoleCompetitionList().clear();
            return;
        }

        Map<Long, RoleCompetitionEntity> existingMap = role.getRoleCompetitionList().stream()
                .collect(Collectors.toMap(rc -> rc.getCompetence().getId(), rc -> rc));

        List<RoleCompetitionEntity> finalList = new ArrayList<>();

        for (RoleCompetitionEntity validatedRc : validatedCompetitions) {
            Long competenceId = validatedRc.getCompetence().getId();

            if (existingMap.containsKey(competenceId)) {
                RoleCompetitionEntity existing = existingMap.get(competenceId);
                existing.setCompetenceImportance(validatedRc.getCompetenceImportance());
                existing.setLevel(validatedRc.getLevel());
                finalList.add(existing);
            } else
                finalList.add(validatedRc);
        }
        role.getRoleCompetitionList().clear();
        role.getRoleCompetitionList().addAll(finalList);
    }

    private List<RoleEntity> processIncompatibleRoles(List<Long> incompatibleRoleIds, RoleEntity currentRole) {
        Set<Long> processedIds = new HashSet<>();

        return incompatibleRoleIds.stream()
                .filter(roleId -> {
                    if (roleId.equals(currentRole.getId())) {
                        throw new BusinessRuleException("Role cannot be incompatible with itself");
                    }
                    return processedIds.add(roleId);
                })
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Incompatible role not found with ID: " + roleId)))
                .toList();
    }

    private RoleDTO.RoleResponseDTO convertToResponseDTO(RoleEntity role) {
        RoleDTO.RoleResponseDTO responseDTO = modelMapper.map(role, RoleDTO.RoleResponseDTO.class);
        responseDTO.setIsBoss(role.isBoss());

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
                    .toList());
        }

        if (role.getIncompatibleRoles() != null) {
            responseDTO.setIncompatibleRoles(role.getIncompatibleRoles().stream()
                    .map(ir -> {
                        RoleDTO.RoleMinimalDTO dto = new RoleDTO.RoleMinimalDTO();
                        dto.setId(ir.getId());
                        dto.setRoleName(ir.getRoleName());
                        return dto;
                    })
                    .toList());
        }

        return responseDTO;
    }
}