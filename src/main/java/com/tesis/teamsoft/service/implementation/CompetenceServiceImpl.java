package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.CompetenceDimensionEntity;
import com.tesis.teamsoft.persistence.entity.CompetenceEntity;
import com.tesis.teamsoft.persistence.entity.LevelsEntity;
import com.tesis.teamsoft.persistence.repository.ICompetenceDimensionRepository;
import com.tesis.teamsoft.persistence.repository.ICompetenceRepository;
import com.tesis.teamsoft.persistence.repository.ILevelsRepository;
import com.tesis.teamsoft.presentation.dto.CompetenceDTO;
import com.tesis.teamsoft.presentation.dto.CompetenceDimensionDTO;
import com.tesis.teamsoft.presentation.dto.LevelsDTO;
import com.tesis.teamsoft.service.interfaces.ICompetenceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

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
    public CompetenceDTO.CompetenceResponseDTO saveCompetence(CompetenceDTO.CompetenceCreateDTO competenceDTO){
        try {
            CompetenceEntity savedCompetence = modelMapper.map(competenceDTO, CompetenceEntity.class);
            savedCompetence.setCompetenceDimensionList(assingCompetenceDimension(competenceDTO.getDimensionList(), savedCompetence));
            return convertToResponseDTO(competenceRepository.save(savedCompetence));

        } catch (TransactionSystemException e) {
            throw new IllegalArgumentException("Error saving competence: Dimension data integrity error: The provided dimension information contains inconsistencies.");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error saving competence: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving competence: " + e.getMessage());
        }
    }

    @Override
    public CompetenceDTO.CompetenceResponseDTO updateCompetence(CompetenceDTO.CompetenceCreateDTO competenceDTO, Long id){

        if(!competenceRepository.existsById(id)){
            throw new RuntimeException("Age group not found with ID: " + id);
        }

        try {
            CompetenceEntity savedCompetence = modelMapper.map(competenceDTO, CompetenceEntity.class);
            savedCompetence.setId(id);
            List<CompetenceDimensionEntity> newDimensions =
                    assingCompetenceDimension(competenceDTO.getDimensionList(), savedCompetence);

            savedCompetence.setCompetenceDimensionList(syncDimensionIds(id, newDimensions));
            return convertToResponseDTO(competenceRepository.save(savedCompetence));

        } catch (TransactionSystemException e) {
            throw new IllegalArgumentException("Error updating competence: Dimension data integrity error: The provided dimension information contains inconsistencies.");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating competence: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating competence: " + e.getMessage());
        }
    }

    @Override
    public String deleteCompetence(Long id){
        CompetenceEntity competence = competenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence not found with ID: " + id));

        // Verificar si tiene relaciones antes de eliminar
        if ((competence.getRoleCompetitionList() != null && !competence.getRoleCompetitionList().isEmpty()) ||
                (competence.getProjectTechCompetenceList() != null && !competence.getProjectTechCompetenceList().isEmpty()) ||
                (competence.getCompetenceValueList() != null && !competence.getCompetenceValueList().isEmpty())) {
            throw new IllegalArgumentException("Cannot delete competence because it has associated relations");
        }

        competenceRepository.deleteById(id);
        return "Competence deleted successfully";
    }

    @Override
    public List<CompetenceDTO.CompetenceResponseDTO> findAllCompetence(){
        try {
            return competenceRepository.findAll()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all competences: " + e.getMessage());
        }
    }

    @Override
    public List<CompetenceDTO.CompetenceResponseDTO> findAllByOrderByIdAsc(){
        try {
            return competenceRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all competences: " + e.getMessage());
        }
    }

    @Override
    public CompetenceDTO.CompetenceResponseDTO findCompetenceById(Long id){
        CompetenceEntity competence = competenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence not found with ID: " + id));

        return convertToResponseDTO(competence);
    }


    private List<CompetenceDimensionEntity> assingCompetenceDimension(
            List<CompetenceDimensionDTO.CompetenceDimensionCreateDTO> dimensionsDTOList,
            CompetenceEntity savedCompetence) {

        // Obtener todos los niveles y crear un mapa rápido
        Map<Long, LevelsEntity> levelsMap = levelsRepository.findAll()
                .stream()
                .collect(Collectors.toMap(LevelsEntity::getId, Function.identity()));

        Set<Long> processedLevelIds = new HashSet<>();
        List<CompetenceDimensionEntity> dimensionList = new ArrayList<>();

        for (CompetenceDimensionDTO.CompetenceDimensionCreateDTO dimensionDTO : dimensionsDTOList) {
            LevelsEntity level = levelsMap.get(dimensionDTO.getLevelsID());

            if (level == null) {
                throw new RuntimeException("Levels not found with ID: " + dimensionDTO.getLevelsID());
            }

            if (!processedLevelIds.add(dimensionDTO.getLevelsID())) {
                throw new IllegalArgumentException("Duplicate level ID: " + dimensionDTO.getLevelsID());
            }

            CompetenceDimensionEntity dimension = new CompetenceDimensionEntity(
                    null, dimensionDTO.getName(), savedCompetence, level);
            dimensionList.add(dimension);
        }

        // Validación final
        if (levelsMap.size() != processedLevelIds.size()) {
            throw new IllegalArgumentException(
                    "Level descriptions must be configured for all competency levels before submission. ");
        }

        return dimensionList;
    }

    /**
     * Sincroniza los IDs de las dimensiones existentes con las nuevas dimensiones
     * Busca las dimensiones existentes por levelId y asigna el ID correspondiente
     */
    private List<CompetenceDimensionEntity> syncDimensionIds(Long competenceId,
                                                             List<CompetenceDimensionEntity> newDimensions) {

        // Obtener dimensiones existentes de la base de datos
        List<CompetenceDimensionEntity> existingDimensions =
                competenceDimensionRepository.findByCompetenceId(competenceId);

        // Crear mapa de dimensiones existentes por levelId
        Map<Long, CompetenceDimensionEntity> existingDimensionsMap = existingDimensions.stream()
                .collect(Collectors.toMap(dim -> dim.getLevel().getId(), Function.identity()));

        // Para cada nueva dimensión, buscar si existe una con el mismo levelId y asignar el ID
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
