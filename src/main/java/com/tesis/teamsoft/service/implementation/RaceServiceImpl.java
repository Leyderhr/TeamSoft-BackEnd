package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.RaceEntity;
import com.tesis.teamsoft.persistence.repository.IRaceRepository;
import com.tesis.teamsoft.presentation.dto.RaceDTO;
import com.tesis.teamsoft.service.interfaces.IRaceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements IRaceService {

    private final IRaceRepository raceRepository;
    private final ModelMapper modelMapper;


    @Override
    public RaceDTO.RaceResponseDTO saveRace(RaceDTO.RaceCreateDTO raceDTO) {
        try {
            RaceEntity savedRace = modelMapper.map(raceDTO, RaceEntity.class);
            return modelMapper.map(raceRepository.save(savedRace), RaceDTO.RaceResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving race: " + e.getMessage());
        }
    }

    @Override
    public RaceDTO.RaceResponseDTO updateRace(RaceDTO.RaceCreateDTO raceDTO, Long id) {

        if (!raceRepository.existsById(id)) {
            throw new RuntimeException("Race not found with ID: " + id);
        }

        try {
            RaceEntity updatedRace = modelMapper.map(raceDTO, RaceEntity.class);
            updatedRace.setId(id);
            return modelMapper.map(raceRepository.save(updatedRace), RaceDTO.RaceResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating race: " + e.getMessage());
        }
    }

    @Override
    public String deleteRace(Long id) {
        RaceEntity race = raceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race not found with ID: " + id));

        // Verificar si tiene personas asociadas antes de eliminar
        if (race.getPersonList() != null && !race.getPersonList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete race because it has associated persons");
        }

        raceRepository.deleteById(id);
        return "Race deleted successfully";
    }

    @Override
    public List<RaceDTO.RaceResponseDTO> findAllRace() {
        try {
            return raceRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RaceDTO.RaceResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all races: " + e.getMessage());
        }
    }

    @Override
    public List<RaceDTO.RaceResponseDTO> findAllByOrderByIdAsc() {
        try {
            return raceRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, RaceDTO.RaceResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all races: " + e.getMessage());
        }
    }

    @Override
    public RaceDTO.RaceResponseDTO findRaceById(Long id) {
        RaceEntity race = raceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race not found with ID: " + id));

        return modelMapper.map(race, RaceDTO.RaceResponseDTO.class);
    }
}