package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.LevelsEntity;
import com.tesis.teamsoft.persistence.repository.ILevelsRepository;
import com.tesis.teamsoft.presentation.dto.LevelsDTO;
import com.tesis.teamsoft.service.interfaces.ILevelsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelsServiceImpl implements ILevelsService {

    private final ILevelsRepository levelsRepository;
    private final ModelMapper modelMapper;

    @Override
    public LevelsDTO.LevelsResponseDTO saveLevels(LevelsDTO.LevelsCreateDTO levelsDTO) {
        try {
            LevelsEntity savedLevels = modelMapper.map(levelsDTO, LevelsEntity.class);
            return modelMapper.map(levelsRepository.save(savedLevels), LevelsDTO.LevelsResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Error saving levels: " + e.getMessage());
        }
    }

    @Override
    public LevelsDTO.LevelsResponseDTO updateLevels(LevelsDTO.LevelsCreateDTO levelsDTO, Long id) {

        if (!levelsRepository.existsById(id)) {
            throw new RuntimeException("Levels not found with ID: " + id);
        }

        try {
            LevelsEntity updatedLevels = modelMapper.map(levelsDTO, LevelsEntity.class);
            updatedLevels.setId(id);
            levelsRepository.save(updatedLevels);
            return modelMapper.map(updatedLevels, LevelsDTO.LevelsResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating levels: " + e.getMessage());
        }
    }

    @Override
    public String deleteLevels(Long id) {
        LevelsEntity levels = levelsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Levels not found with ID: " + id));

        // Verificar si tiene relaciones antes de eliminar
        if ((levels.getRoleCompetitionList() != null && !levels.getRoleCompetitionList().isEmpty()) ||
                (levels.getProjectTechCompetenceList() != null && !levels.getProjectTechCompetenceList().isEmpty()) ||
                (levels.getCompetenceValueList() != null && !levels.getCompetenceValueList().isEmpty()) ||
                (levels.getCompetenceDimensionList() != null && !levels.getCompetenceDimensionList().isEmpty())) {
            throw new IllegalArgumentException("Cannot delete levels because it has associated relations");
        }

        levelsRepository.deleteById(id);
        return "Levels deleted successfully";
    }

    @Override
    public List<LevelsDTO.LevelsResponseDTO> findAllLevels() {
        try {
            return levelsRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, LevelsDTO.LevelsResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all levels: " + e.getMessage());
        }
    }

    @Override
    public List<LevelsDTO.LevelsResponseDTO> findAllByOrderByIdAsc() {
        try {
            return levelsRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, LevelsDTO.LevelsResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all levels: " + e.getMessage());
        }
    }

    @Override
    public LevelsDTO.LevelsResponseDTO findLevelsById(Long id) {
        LevelsEntity levels = levelsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Levels not found with ID: " + id));

        return modelMapper.map(levels, LevelsDTO.LevelsResponseDTO.class);
    }
}