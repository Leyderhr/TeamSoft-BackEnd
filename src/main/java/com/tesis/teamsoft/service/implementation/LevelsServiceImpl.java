package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.LevelsEntity;
import com.tesis.teamsoft.persistence.repository.ILevelsRepository;
import com.tesis.teamsoft.presentation.dto.LevelsDTO;
import com.tesis.teamsoft.service.interfaces.ILevelsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelsServiceImpl implements ILevelsService {

    private final ILevelsRepository levelsRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LevelsDTO.LevelsResponseDTO saveLevels(LevelsDTO.LevelsCreateDTO levelsDTO) {
            LevelsEntity savedLevels = modelMapper.map(levelsDTO, LevelsEntity.class);
            return modelMapper.map(levelsRepository.save(savedLevels), LevelsDTO.LevelsResponseDTO.class);
    }

    @Override
    @Transactional
    public LevelsDTO.LevelsResponseDTO updateLevels(LevelsDTO.LevelsCreateDTO levelsDTO, Long id) {
        if (!levelsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Levels not found with ID: " + id);
        }
        LevelsEntity updatedLevels = modelMapper.map(levelsDTO, LevelsEntity.class);
        updatedLevels.setId(id);
        return modelMapper.map(levelsRepository.save(updatedLevels), LevelsDTO.LevelsResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteLevels(Long id) {
        LevelsEntity levels = levelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Levels not found with ID: " + id));

        if ((levels.getRoleCompetitionList() != null && !levels.getRoleCompetitionList().isEmpty()) ||
                (levels.getProjectTechCompetenceList() != null && !levels.getProjectTechCompetenceList().isEmpty()) ||
                (levels.getCompetenceValueList() != null && !levels.getCompetenceValueList().isEmpty()) ||
                (levels.getCompetenceDimensionList() != null && !levels.getCompetenceDimensionList().isEmpty())) {
            throw new BusinessRuleException("Cannot delete levels because it has associated relations");
        }

        levelsRepository.deleteById(id);
        return "Levels deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<LevelsDTO.LevelsResponseDTO> findAllLevels() {
        return levelsRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, LevelsDTO.LevelsResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LevelsDTO.LevelsResponseDTO> findAllByOrderByIdAsc() {
        return levelsRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, LevelsDTO.LevelsResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LevelsDTO.LevelsResponseDTO findLevelsById(Long id) {
        LevelsEntity levels = levelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Levels not found with ID: " + id));
        return modelMapper.map(levels, LevelsDTO.LevelsResponseDTO.class);
    }
}