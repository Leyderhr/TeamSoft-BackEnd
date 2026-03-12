package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.ReligionEntity;
import com.tesis.teamsoft.persistence.repository.IReligionRepository;
import com.tesis.teamsoft.presentation.dto.ReligionDTO;
import com.tesis.teamsoft.service.interfaces.IReligionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReligionServiceImpl implements IReligionService {

    private final IReligionRepository religionRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ReligionDTO.ReligionResponseDTO saveReligion(ReligionDTO.ReligionCreateDTO religionDTO) {
        ReligionEntity savedReligion = modelMapper.map(religionDTO, ReligionEntity.class);
        religionRepository.save(savedReligion);
        return modelMapper.map(savedReligion, ReligionDTO.ReligionResponseDTO.class);
    }

    @Override
    @Transactional
    public ReligionDTO.ReligionResponseDTO updateReligion(ReligionDTO.ReligionCreateDTO religionDTO, Long id) {
        if (!religionRepository.existsById(id))
            throw new ResourceNotFoundException("Religion not found with ID: " + id);

        ReligionEntity savedReligion = modelMapper.map(religionDTO, ReligionEntity.class);
        savedReligion.setId(id);
        religionRepository.save(savedReligion);
        return modelMapper.map(savedReligion, ReligionDTO.ReligionResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteReligion(Long id) {
        ReligionEntity religion = religionRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Religion not found with id: " + id));

        if(religion.getPersonList() != null && !religion.getPersonList().isEmpty())
            throw new BusinessRuleException("Cannot delete religion because it has associated persons");

        religionRepository.deleteById(id);
        return "Religion deleted";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReligionDTO.ReligionResponseDTO> findAllReligion() {
        return religionRepository.findAll()
                .stream()
                .map(religionEntity -> modelMapper.map(religionEntity, ReligionDTO.ReligionResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReligionDTO.ReligionResponseDTO> findAllByOrderByIdAsc() {
        return religionRepository.findAllByOrderByIdAsc()
                .stream()
                .map(religionEntity -> modelMapper.map(religionEntity, ReligionDTO.ReligionResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReligionDTO.ReligionResponseDTO findReligionById(Long id) {
        ReligionEntity religion = religionRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Religion not found with ID: " + id));

        return modelMapper.map(religion, ReligionDTO.ReligionResponseDTO.class);
    }
}
