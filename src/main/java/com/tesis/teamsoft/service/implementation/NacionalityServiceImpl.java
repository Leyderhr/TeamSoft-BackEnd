package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
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
        validateUniqueAttributes(nacionalityDTO, null);
        return modelMapper.map(nacionalityRepository.save(savedNacionality), NationalityDTO.NacionalityResponseDTO.class);
    }

    @Override
    @Transactional
    public NationalityDTO.NacionalityResponseDTO updateNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO, Long id) {
        if (!nacionalityRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_NATIONALITY_NOT_FOUND", id);
        }
        NacionalityEntity updatedNacionality = modelMapper.map(nacionalityDTO, NacionalityEntity.class);
        updatedNacionality.setId(id);
        validateUniqueAttributes(nacionalityDTO, id);
        return modelMapper.map(nacionalityRepository.save(updatedNacionality), NationalityDTO.NacionalityResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteNacionality(Long id) {
        NacionalityEntity nacionality = nacionalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_NATIONALITY_NOT_FOUND", id));

        if (nacionality.getPersonList() != null && !nacionality.getPersonList().isEmpty()) {
            throw new BusinessRuleException("ERR_NATIONALITY_CANT_BE_DELETED");
        }

        nacionalityRepository.deleteById(id);
        return "NATIONALITY_SUCCESSFULLY_DELETED";
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
                .orElseThrow(() -> new ResourceNotFoundException("ERR_NATIONALITY_NOT_FOUND", id));
        return modelMapper.map(nacionality, NationalityDTO.NacionalityResponseDTO.class);
    }

    private void validateUniqueAttributes(NationalityDTO.NacionalityCreateDTO dto, Long id) {
        boolean paisExists = (id == null) ?
                nacionalityRepository.existsByPaisNac(dto.getPaisNac()) :
                nacionalityRepository.existsByPaisNacAndIdNot(dto.getPaisNac(), id);
        if (paisExists) {
            throw new DuplicateResourceException("ERR_NATIONALITY_COUNTRY_ALREADY_EXISTS");
        }

        boolean gentilicioExists = (id == null) ?
                nacionalityRepository.existsByGentilicioNac(dto.getGentilicioNac()) :
                nacionalityRepository.existsByGentilicioNacAndIdNot(dto.getGentilicioNac(), id);
        if (gentilicioExists) {
            throw new DuplicateResourceException("ERR_NATIONALITY_DEMONYM_ALREADY_EXISTS");
        }
    }
}