package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.presentation.dto.CompetenceDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

@Repository
public interface ICompetenceService {
    CompetenceDTO.CompetenceResponseDTO saveCompetence(CompetenceDTO.CompetenceCreateDTO competenceDTO);

    CompetenceDTO.CompetenceResponseDTO updateCompetence(CompetenceDTO.CompetenceCreateDTO ageGroupDTO, Long id);

    String deleteCompetence(Long id);

    List<CompetenceDTO.CompetenceResponseDTO> findAllCompetence();

    List<CompetenceDTO.CompetenceResponseDTO> findAllByOrderByIdAsc();

    CompetenceDTO.CompetenceResponseDTO findCompetenceById(Long id);

    ImportResultDTO importCompetences(InputStream inputStream, boolean updateIfExist);

    InputStream exportCompetences();
}
