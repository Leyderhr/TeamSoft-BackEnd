package com.tesis.teamsoft.service.implementation;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.CompetenceDimensionEntity;
import com.tesis.teamsoft.persistence.entity.CompetenceEntity;
import com.tesis.teamsoft.persistence.entity.LevelsEntity;
import com.tesis.teamsoft.persistence.repository.ICompetenceDimensionRepository;
import com.tesis.teamsoft.persistence.repository.ICompetenceRepository;
import com.tesis.teamsoft.persistence.repository.ILevelsRepository;
import com.tesis.teamsoft.presentation.dto.CompetenceDTO;
import com.tesis.teamsoft.presentation.dto.CompetenceDimensionDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import com.tesis.teamsoft.presentation.dto.LevelsDTO;
import com.tesis.teamsoft.service.interfaces.ICompetenceService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetenceServiceImpl implements ICompetenceService {

    private final ICompetenceRepository competenceRepository;
    private final ILevelsRepository levelsRepository;
    private final ICompetenceDimensionRepository competenceDimensionRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CompetenceDTO.CompetenceResponseDTO saveCompetence(CompetenceDTO.CompetenceCreateDTO competenceDTO){

        CompetenceEntity savedCompetence = modelMapper.map(competenceDTO, CompetenceEntity.class);
        savedCompetence.setCompetenceDimensionList(assingCompetenceDimension(competenceDTO.getDimensionList(), savedCompetence));
        return convertToResponseDTO(competenceRepository.save(savedCompetence));

    }

    @Override
    @Transactional
    public CompetenceDTO.CompetenceResponseDTO updateCompetence(CompetenceDTO.CompetenceCreateDTO competenceDTO, Long id){
        if(!competenceRepository.existsById(id)){
            throw new ResourceNotFoundException("Competence not found with ID: " + id);
        }

        CompetenceEntity savedCompetence = modelMapper.map(competenceDTO, CompetenceEntity.class);
        savedCompetence.setId(id);
        List<CompetenceDimensionEntity> newDimensions =
                assingCompetenceDimension(competenceDTO.getDimensionList(), savedCompetence);
        savedCompetence.setCompetenceDimensionList(syncDimensionIds(id, newDimensions));
        return convertToResponseDTO(competenceRepository.save(savedCompetence));
    }

    @Override
    @Transactional
    public String deleteCompetence(Long id){
        CompetenceEntity competence = competenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competence not found with ID: " + id));

        if ((competence.getRoleCompetitionList() != null && !competence.getRoleCompetitionList().isEmpty()) ||
                (competence.getProjectTechCompetenceList() != null && !competence.getProjectTechCompetenceList().isEmpty()) ||
                (competence.getCompetenceValueList() != null && !competence.getCompetenceValueList().isEmpty())) {
            throw new BusinessRuleException("Cannot delete competence because it has associated relations");
        }

        competenceRepository.deleteById(id);
        return "Competence deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetenceDTO.CompetenceResponseDTO> findAllCompetence(){
        return competenceRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetenceDTO.CompetenceResponseDTO> findAllByOrderByIdAsc(){
        return competenceRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompetenceDTO.CompetenceResponseDTO findCompetenceById(Long id){
        CompetenceEntity competence = competenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competence not found with ID: " + id));
        return convertToResponseDTO(competence);
    }

    @Override
    @Transactional
    public ImportResultDTO importCompetences(InputStream inputStream, boolean updateIfExist) {
        ImportResultDTO result = new ImportResultDTO();

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<CompetenceDTO.CompetenceImportExportDTO> rows = new CsvToBeanBuilder<CompetenceDTO.CompetenceImportExportDTO>(reader)
                    .withType(CompetenceDTO.CompetenceImportExportDTO.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            for (CompetenceDTO.CompetenceImportExportDTO row : rows) {
                try {
                    // Validaciones
                    if (row.getCompetitionName() == null || row.getCompetitionName().trim().isEmpty()) {
                        throw new IllegalArgumentException("El nombre de la competencia no puede estar vacío");
                    }
                    if (row.getDescription() == null || row.getDescription().trim().isEmpty()) {
                        throw new IllegalArgumentException("La descripción no puede estar vacía");
                    }

                    CompetenceEntity existing = competenceRepository.findByCompetitionName(row.getCompetitionName()).orElse(null);
                    if (existing != null) {
                        if (updateIfExist) {
                            existing.setDescription(row.getDescription());
                            existing.setTechnical(row.isTechnical());
                            competenceRepository.save(existing);
                            result.setUpdated(result.getUpdated() + 1);
                        } else {
                            result.setSkipped(result.getSkipped() + 1);
                        }
                    } else {
                        CompetenceEntity newCompetence = modelMapper.map(row, CompetenceEntity.class);
                        competenceRepository.save(newCompetence);
                        result.setCreated(result.getCreated() + 1);
                    }
                } catch (Exception e) {
                    result.setErrors(result.getErrors() + 1);
                    result.getErrorMessages().add("Error en fila con nombre '" + row.getCompetitionName() + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo", e);
        }
        return result;
    }

    @Override
    public InputStream exportCompetences() {
        List<CompetenceEntity> competences = competenceRepository.findAllByOrderByIdAsc();
        List<CompetenceDTO.CompetenceImportExportDTO> exportData = competences.stream()
                .map(c -> {
                    CompetenceDTO.CompetenceImportExportDTO dto = new CompetenceDTO.CompetenceImportExportDTO();
                    dto.setCompetitionName(c.getCompetitionName());
                    dto.setDescription(c.getDescription());
                    dto.setTechnical(c.getTechnical());
                    return dto;
                })
                .toList();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            StatefulBeanToCsv<CompetenceDTO.CompetenceImportExportDTO> beanToCsv = new StatefulBeanToCsvBuilder<CompetenceDTO.CompetenceImportExportDTO>(writer)
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


    private List<CompetenceDimensionEntity> assingCompetenceDimension(
            List<CompetenceDimensionDTO.CompetenceDimensionCreateDTO> dimensionsDTOList,
            CompetenceEntity savedCompetence) {
        Map<Long, LevelsEntity> levelsMap = levelsRepository.findAll()
                .stream()
                .collect(Collectors.toMap(LevelsEntity::getId, Function.identity()));

        Set<Long> processedLevelIds = new HashSet<>();
        List<CompetenceDimensionEntity> dimensionList = new ArrayList<>();

        for (CompetenceDimensionDTO.CompetenceDimensionCreateDTO dimensionDTO : dimensionsDTOList) {
            LevelsEntity level = levelsMap.get(dimensionDTO.getLevelsID());

            if (level == null)
                throw new ResourceNotFoundException("Levels not found with ID: " + dimensionDTO.getLevelsID());

            if (!processedLevelIds.add(dimensionDTO.getLevelsID()))
                throw new BusinessRuleException("Duplicate level ID: " + dimensionDTO.getLevelsID());

            CompetenceDimensionEntity dimension = new CompetenceDimensionEntity(
                    null, dimensionDTO.getName(), savedCompetence, level);
            dimensionList.add(dimension);
        }
        if (levelsMap.size() != processedLevelIds.size())
            throw new IllegalArgumentException(
                    "Level descriptions must be configured for all competency levels before submission. ");

        return dimensionList;
    }

    private List<CompetenceDimensionEntity> syncDimensionIds(Long competenceId,
                                                             List<CompetenceDimensionEntity> newDimensions) {
        List<CompetenceDimensionEntity> existingDimensions = competenceDimensionRepository.findByCompetenceId(competenceId);
        Map<Long, CompetenceDimensionEntity> existingDimensionsMap = existingDimensions.stream()
                .collect(Collectors.toMap(dim -> dim.getLevel().getId(), Function.identity()));

        for (CompetenceDimensionEntity newDimension : newDimensions) {
            Long levelId = newDimension.getLevel().getId();
            if (existingDimensionsMap.containsKey(levelId)) {
                newDimension.setId(existingDimensionsMap.get(levelId).getId());
            }
        }

        return newDimensions;
    }

    private CompetenceDTO.CompetenceResponseDTO convertToResponseDTO(CompetenceEntity competence){
        CompetenceDTO.CompetenceResponseDTO responseDTO = modelMapper.map(competence, CompetenceDTO.CompetenceResponseDTO.class);
        List<CompetenceDimensionDTO.CompetenceDimensionResponseDTO> dimensionDTOList = new ArrayList<>();

        for(CompetenceDimensionEntity cd: competence.getCompetenceDimensionList()){
            CompetenceDimensionDTO.CompetenceDimensionResponseDTO dto =
                    new CompetenceDimensionDTO.CompetenceDimensionResponseDTO();
            dto.setName(cd.getName());
            dto.setCompetenceID(competence.getId());
            dto.setLevelsFk(modelMapper.map(cd.getLevel(), LevelsDTO.LevelsResponseDTO.class));
            dimensionDTOList.add(dto);
        }
        responseDTO.setDimensionList(dimensionDTOList);
        return responseDTO;
    }
}
