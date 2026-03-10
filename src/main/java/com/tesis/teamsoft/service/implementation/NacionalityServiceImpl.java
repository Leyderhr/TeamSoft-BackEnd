package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.NacionalityEntity;
import com.tesis.teamsoft.persistence.repository.INacionalityRepository;
import com.tesis.teamsoft.presentation.dto.NationalityDTO;
import com.tesis.teamsoft.service.interfaces.INacionalityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NacionalityServiceImpl implements INacionalityService {

    private final INacionalityRepository nacionalityRepository;
    private final ModelMapper modelMapper;

    @Override
    public NationalityDTO.NacionalityResponseDTO saveNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO) {
        try {
            NacionalityEntity savedNacionality = modelMapper.map(nacionalityDTO, NacionalityEntity.class);
            return modelMapper.map(nacionalityRepository.save(savedNacionality), NationalityDTO.NacionalityResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving nationality: " + e.getMessage());
        }
    }

    @Override
    public NationalityDTO.NacionalityResponseDTO updateNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO, Long id) {

        if (!nacionalityRepository.existsById(id)) {
            throw new RuntimeException("Nationality not found with ID: " + id);
        }

        try {
            NacionalityEntity updatedNacionality = modelMapper.map(nacionalityDTO, NacionalityEntity.class);
            updatedNacionality.setId(id);
            return modelMapper.map(nacionalityRepository.save(updatedNacionality), NationalityDTO.NacionalityResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating nationality: " + e.getMessage());
        }
    }

    @Override
    public String deleteNacionality(Long id) {
        NacionalityEntity nacionality = nacionalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nationality not found with ID: " + id));

        // Verificar si tiene personas asociadas antes de eliminar
        if (nacionality.getPersonList() != null && !nacionality.getPersonList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete nationality because it has associated persons");
        }

        nacionalityRepository.deleteById(id);
        return "Nationality deleted successfully";
    }

    @Override
    public List<NationalityDTO.NacionalityResponseDTO> findAllNacionality() {
        try {
            return nacionalityRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, NationalityDTO.NacionalityResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all nationalities: " + e.getMessage());
        }
    }

    @Override
    public List<NationalityDTO.NacionalityResponseDTO> findAllByOrderByIdAsc() {
        try {
            return nacionalityRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, NationalityDTO.NacionalityResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all nationalities ordered by ID: " + e.getMessage());
        }
    }

    @Override
    public NationalityDTO.NacionalityResponseDTO findNacionalityById(Long id) {
        NacionalityEntity nacionality = nacionalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nationality not found with ID: " + id));

        return modelMapper.map(nacionality, NationalityDTO.NacionalityResponseDTO.class);
    }
}