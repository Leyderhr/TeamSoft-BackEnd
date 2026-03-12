package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.RaceEntity;
import com.tesis.teamsoft.persistence.repository.IRaceRepository;
import com.tesis.teamsoft.presentation.dto.RaceDTO;
import com.tesis.teamsoft.service.interfaces.IRaceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements IRaceService {

    private final IRaceRepository raceRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public RaceDTO.RaceResponseDTO saveRace(RaceDTO.RaceCreateDTO raceDTO) {
        RaceEntity savedRace = modelMapper.map(raceDTO, RaceEntity.class);
        return modelMapper.map(raceRepository.save(savedRace), RaceDTO.RaceResponseDTO.class);
    }

    @Override
    @Transactional
    public RaceDTO.RaceResponseDTO updateRace(RaceDTO.RaceCreateDTO raceDTO, Long id) {
        if (!raceRepository.existsById(id))
            throw new ResourceNotFoundException("Race not found with ID: " + id);

        RaceEntity updatedRace = modelMapper.map(raceDTO, RaceEntity.class);
        updatedRace.setId(id);
        return modelMapper.map(raceRepository.save(updatedRace), RaceDTO.RaceResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteRace(Long id) {
        RaceEntity race = raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with ID: " + id));

        if (race.getPersonList() != null && !race.getPersonList().isEmpty())
            throw new BusinessRuleException("Cannot delete race because it has associated persons");

        raceRepository.deleteById(id);
        return "Race deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RaceDTO.RaceResponseDTO> findAllRace() {
        return raceRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, RaceDTO.RaceResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RaceDTO.RaceResponseDTO> findAllByOrderByIdAsc() {
        return raceRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, RaceDTO.RaceResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RaceDTO.RaceResponseDTO findRaceById(Long id) {
        RaceEntity race = raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with ID: " + id));
        return modelMapper.map(race, RaceDTO.RaceResponseDTO.class);
    }
}