package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.CompetenceImportanceEntity;
import com.tesis.teamsoft.persistence.repository.ICompetenceImportanceRepository;
import com.tesis.teamsoft.presentation.dto.CompetenceImportanceDTO;
import com.tesis.teamsoft.service.interfaces.ICompetenceImportanceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetenceImportanceServiceImpl implements ICompetenceImportanceService {

    private final ICompetenceImportanceRepository competenceImportanceRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO saveCompetenceImportance(
            CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO) {

        CompetenceImportanceEntity savedCompetenceImportance = modelMapper.map(competenceImportanceDTO, CompetenceImportanceEntity.class);
        validateUniqueFields(savedCompetenceImportance, null);
        return modelMapper.map(competenceImportanceRepository.save(savedCompetenceImportance), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
    }

    @Override
    @Transactional
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO updateCompetenceImportance(
            CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO, Long id) {

        if (!competenceImportanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_COMP_IMPORTANCE_NOT_FOUND", id);
        }

        CompetenceImportanceEntity updatedCompetenceImportance = modelMapper.map(competenceImportanceDTO, CompetenceImportanceEntity.class);
        updatedCompetenceImportance.setId(id);
        validateUniqueFields(updatedCompetenceImportance, id);
        return modelMapper.map(competenceImportanceRepository.save(updatedCompetenceImportance), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);

    }

    @Override
    @Transactional
    public String deleteCompetenceImportance(Long id) {
        CompetenceImportanceEntity competenceImportance = competenceImportanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_COMP_IMPORTANCE_NOT_FOUND", id));

        if ((competenceImportance.getRoleCompetitionList() != null && !competenceImportance.getRoleCompetitionList().isEmpty()) ||
                (competenceImportance.getProjectTechCompetenceList() != null && !competenceImportance.getProjectTechCompetenceList().isEmpty())) {
            throw new BusinessRuleException("ERR_COMP_IMPORTANCE_CANT_BE_DELETED", id);
        }

        competenceImportanceRepository.deleteById(id);
        return "COMPIMPORTANCE_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> findAllCompetenceImportance() {
        return competenceImportanceRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> findAllByOrderByIdAsc() {
        return competenceImportanceRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO findCompetenceImportanceById(Long id) {
        CompetenceImportanceEntity competenceImportance = competenceImportanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_COMP_IMPORTANCE_NOT_FOUND", id));
        return modelMapper.map(competenceImportance, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
    }

    private void validateUniqueFields(CompetenceImportanceEntity entity, Long id) {
        boolean existLevels = (id == null) ?
                competenceImportanceRepository.existsByLevels(entity.getLevels()) :
                competenceImportanceRepository.existsByLevelsAndIdNot(entity.getLevels(), id);
        if (existLevels) {
                throw new DuplicateResourceException("ERR_COMP_IMPORTANCE_LEVELS_DUPLICATE");
        }

        boolean existeSignificance = (id == null) ?
                competenceImportanceRepository.existsBySignificance(entity.getSignificance()) :
                competenceImportanceRepository.existsBySignificanceAndIdNot(entity.getSignificance(), id);
        if (existeSignificance) {
                throw new DuplicateResourceException("ERR_COMP_IMPORTANCE_SIGNIFICANCE_DUPLICATE");
        }
    }
}