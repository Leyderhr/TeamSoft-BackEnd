package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.ReligionEntity;
import com.tesis.teamsoft.persistence.repository.IReligionRepository;
import com.tesis.teamsoft.presentation.dto.ReligionDTO;
import com.tesis.teamsoft.service.interfaces.IReligionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReligionServiceImpl implements IReligionService {

    private final IReligionRepository religionRepository;
    private final ModelMapper modelMapper;


    @Override
    public ReligionDTO.ReligionResponseDTO saveReligion(ReligionDTO.ReligionCreateDTO religionDTO) {

        try {
            ReligionEntity savedReligion = modelMapper.map(religionDTO, ReligionEntity.class);
            religionRepository.save(savedReligion);
            return modelMapper.map(savedReligion, ReligionDTO.ReligionResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Error saving religion: " + e.getMessage());
        }


    }

    @Override
    public ReligionDTO.ReligionResponseDTO updateReligion(ReligionDTO.ReligionCreateDTO religionDTO, Long id) {

        if (!religionRepository.existsById(id)) {
            throw new RuntimeException("Religion not found with ID: " + id);
        }

        try {
            ReligionEntity savedReligion = modelMapper.map(religionDTO, ReligionEntity.class);
            savedReligion.setId(id);
            religionRepository.save(savedReligion);
            return modelMapper.map(savedReligion, ReligionDTO.ReligionResponseDTO.class);

        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating religion: " + e.getMessage());
        }
    }

    @Override
    public String deleteReligion(Long id) {

        if (!religionRepository.existsById(id)) {
            throw new RuntimeException("Religion not found with ID: " + id);
        }
        religionRepository.deleteById(id);
        return "Religion deleted";
    }

    @Override
    public List<ReligionDTO.ReligionResponseDTO> findAllReligion() {
        try {
            return religionRepository.findAll()
                    .stream()
                    .map(religionEntity -> modelMapper.map(religionEntity, ReligionDTO.ReligionResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all religions " + e.getMessage());
        }
    }

    @Override
    public List<ReligionDTO.ReligionResponseDTO> findAllByOrderByIdAsc() {
        try {

            return religionRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(religionEntity -> modelMapper.map(religionEntity, ReligionDTO.ReligionResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all religions: " + e.getMessage());
        }
    }

    @Override
    public ReligionDTO.ReligionResponseDTO findReligionById(Long id) {

        ReligionEntity religion = religionRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Religion not found with ID: " + id));

        return modelMapper.map(religion, ReligionDTO.ReligionResponseDTO.class);
    }
}
