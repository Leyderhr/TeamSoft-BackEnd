package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.CompetenceImportanceEntity;
import com.tesis.teamsoft.persistence.repository.ICompetenceImportanceRepository;
import com.tesis.teamsoft.presentation.dto.CompetenceImportanceDTO;
import com.tesis.teamsoft.service.interfaces.ICompetenceImportanceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetenceImportanceServiceImpl implements ICompetenceImportanceService {

    private final ICompetenceImportanceRepository competenceImportanceRepository;
    private final ModelMapper modelMapper;


    @Override
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO saveCompetenceImportance(
            CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO) {
        try {
            CompetenceImportanceEntity savedCompetenceImportance = modelMapper.map(competenceImportanceDTO, CompetenceImportanceEntity.class);
            return modelMapper.map(competenceImportanceRepository.save(savedCompetenceImportance), CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving competence importance: " + e.getMessage());
        }
    }

    @Override
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO updateCompetenceImportance(
            CompetenceImportanceDTO.CompetenceImportanceCreateDTO competenceImportanceDTO, Long id) {

        if (!competenceImportanceRepository.existsById(id)) {
            throw new RuntimeException("Competence importance not found with ID: " + id);
        }

        try {
            CompetenceImportanceEntity updatedCompetenceImportance = modelMapper.map(competenceImportanceDTO, CompetenceImportanceEntity.class);
            updatedCompetenceImportance.setId(id);
            competenceImportanceRepository.save(updatedCompetenceImportance);
            return modelMapper.map(updatedCompetenceImportance, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating competence importance: " + e.getMessage());
        }
    }

    @Override
    public String deleteCompetenceImportance(Long id) {

        CompetenceImportanceEntity competenceImportance = competenceImportanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence importance not found with ID: " + id));

        // Verificar si tiene relaciones antes de eliminar
        if ((competenceImportance.getRoleCompetitionList() != null && !competenceImportance.getRoleCompetitionList().isEmpty()) ||
                (competenceImportance.getProjectTechCompetenceList() != null && !competenceImportance.getProjectTechCompetenceList().isEmpty())) {
            throw new IllegalArgumentException("Cannot delete competence importance because it has associated relations");
        }

        competenceImportanceRepository.deleteById(id);
        return "Competence importance deleted successfully";
    }

    @Override
    public List<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> findAllCompetenceImportance() {
        try {
            return competenceImportanceRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all competence importances: " + e.getMessage());
        }
    }

    @Override
    public List<CompetenceImportanceDTO.CompetenceImportanceResponseDTO> findAllByOrderByIdAsc() {
        try {
            return competenceImportanceRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all competence importances: " + e.getMessage());
        }
    }

    @Override
    public CompetenceImportanceDTO.CompetenceImportanceResponseDTO findCompetenceImportanceById(Long id) {
        CompetenceImportanceEntity competenceImportance = competenceImportanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence importance not found with ID: " + id));

        return modelMapper.map(competenceImportance, CompetenceImportanceDTO.CompetenceImportanceResponseDTO.class);
    }
}