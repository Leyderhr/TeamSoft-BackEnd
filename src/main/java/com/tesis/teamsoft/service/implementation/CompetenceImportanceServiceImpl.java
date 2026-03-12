package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
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
        return modelMapper.map(competenceImportanceRepository.save(savedCompetenceImportance), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
    }

    @Override
    @Transactional
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO updateCompetenceImportance(
            CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO, Long id) {

        if (!competenceImportanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Competence importance not found with ID: " + id);
        }

        CompetenceImportanceEntity updatedCompetenceImportance = modelMapper.map(competenceImportanceDTO, CompetenceImportanceEntity.class);
        updatedCompetenceImportance.setId(id);
        return modelMapper.map(competenceImportanceRepository.save(updatedCompetenceImportance), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);

    }

    @Override
    @Transactional
    public String deleteCompetenceImportance(Long id) {
        CompetenceImportanceEntity competenceImportance = competenceImportanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competence importance not found with ID: " + id));

        if ((competenceImportance.getRoleCompetitionList() != null && !competenceImportance.getRoleCompetitionList().isEmpty()) ||
                (competenceImportance.getProjectTechCompetenceList() != null && !competenceImportance.getProjectTechCompetenceList().isEmpty())) {
            throw new BusinessRuleException("Cannot delete competence importance because it has associated relations");
        }

        competenceImportanceRepository.deleteById(id);
        return "Competence importance deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("Competence importance not found with ID: " + id));
        return modelMapper.map(competenceImportance, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
    }
}