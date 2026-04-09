package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    @Transactional
    @Override
    public ImportResultDTO importRoles(InputStream inputStream, boolean updateIfExist) {
        ImportResultDTO result = new ImportResultDTO();

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<RoleDTO.RoleImportExportDTO> rows = new CsvToBeanBuilder<RoleDTO.RoleImportExportDTO>(reader)
                    .withType(RoleDTO.RoleImportExportDTO.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            for (RoleDTO.RoleImportExportDTO row : rows) {
                try {
                    // Validaciones básicas
                    if (row.getRoleName() == null || row.getRoleName().trim().isEmpty()) {
                        throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
                    }
                    if (row.getRoleDesc() == null || row.getRoleDesc().trim().isEmpty()) {
                        throw new IllegalArgumentException("La descripción no puede estar vacía");
                    }
                    if (row.getImpact() < 0) {
                        throw new IllegalArgumentException("El impacto debe ser >= 0");
                    }

                    RoleEntity existing = roleRepository.findByRoleName(row.getRoleName()).orElse(null);

                    if (existing != null) {
                        if (updateIfExist) {
                            existing.setRoleDesc(row.getRoleDesc());
                            existing.setImpact(row.getImpact());
                            existing.setBoss(row.isBoss());
                            roleRepository.save(existing);
                            result.setUpdated(result.getUpdated() + 1);
                        } else {
                            result.setSkipped(result.getSkipped() + 1);
                        }
                    } else {
                        // Crear nuevo
                        RoleEntity newRole = modelMapper.map(row, RoleEntity.class);
                        roleRepository.save(newRole);
                        result.setCreated(result.getCreated() + 1);
                    }
                } catch (Exception e) {
                    result.setErrors(result.getErrors() + 1);
                    result.getErrorMessages().add("Error en fila con nombre '" + row.getRoleName() + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo", e);
        }
        return result;
    }
    
    @Override
    public InputStream exportRoles() {
        List<RoleEntity> roles = roleRepository.findAllByOrderByIdAsc();
        List<RoleDTO.RoleImportExportDTO> exportData = roles.stream()
                .map(r -> {
                    return modelMapper.map(r, RoleDTO.RoleImportExportDTO.class);
                })
                .toList();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            StatefulBeanToCsv<RoleDTO.RoleImportExportDTO> beanToCsv = new StatefulBeanToCsvBuilder<RoleDTO.RoleImportExportDTO>(writer)
                    .withSeparator(';')
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(exportData);
            writer.flush();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error generando el archivo CSV", e);
        }
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