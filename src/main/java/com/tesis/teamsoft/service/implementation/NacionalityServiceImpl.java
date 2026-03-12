package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.NacionalityEntity;
import com.tesis.teamsoft.persistence.repository.INacionalityRepository;
import com.tesis.teamsoft.presentation.dto.NationalityDTO;
import com.tesis.teamsoft.service.interfaces.INacionalityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NacionalityServiceImpl implements INacionalityService {

    private final INacionalityRepository nacionalityRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public NationalityDTO.NacionalityResponseDTO saveNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO) {
        NacionalityEntity savedNacionality = modelMapper.map(nacionalityDTO, NacionalityEntity.class);
        return modelMapper.map(nacionalityRepository.save(savedNacionality), NationalityDTO.NacionalityResponseDTO.class);
    }

    @Override
    @Transactional
    public NationalityDTO.NacionalityResponseDTO updateNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO, Long id) {
        if (!nacionalityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nationality not found with ID: " + id);
        }
        NacionalityEntity updatedNacionality = modelMapper.map(nacionalityDTO, NacionalityEntity.class);
        updatedNacionality.setId(id);
        return modelMapper.map(nacionalityRepository.save(updatedNacionality), NationalityDTO.NacionalityResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteNacionality(Long id) {
        NacionalityEntity nacionality = nacionalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nationality not found with ID: " + id));

        if (nacionality.getPersonList() != null && !nacionality.getPersonList().isEmpty()) {
            throw new BusinessRuleException("Cannot delete nationality because it has associated persons");
        }

        nacionalityRepository.deleteById(id);
        return "Nationality deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<NationalityDTO.NacionalityResponseDTO> findAllNacionality() {
        return nacionalityRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, NationalityDTO.NacionalityResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NationalityDTO.NacionalityResponseDTO> findAllByOrderByIdAsc() {
        return nacionalityRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, NationalityDTO.NacionalityResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NationalityDTO.NacionalityResponseDTO findNacionalityById(Long id) {
        NacionalityEntity nacionality = nacionalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nationality not found with ID: " + id));
        return modelMapper.map(nacionality, NationalityDTO.NacionalityResponseDTO.class);
    }
}