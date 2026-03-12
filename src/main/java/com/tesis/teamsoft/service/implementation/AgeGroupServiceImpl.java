package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.AgeGroupEntity;
import com.tesis.teamsoft.persistence.repository.IAgeGroupRepository;
import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.service.interfaces.IAgeGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgeGroupServiceImpl implements IAgeGroupService {


    private final IAgeGroupRepository ageGroupRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public AgeGroupDTO.AgeGroupResponseDTO saveAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO) {
        AgeGroupEntity savedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
        validateNonOverlappingAgeRange(savedAgeGroup);
        return modelMapper.map(ageGroupRepository.save(savedAgeGroup), AgeGroupDTO.AgeGroupResponseDTO.class);

    }

    @Override
    @Transactional
    public AgeGroupDTO.AgeGroupResponseDTO updateAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO, Long id){
        if (!ageGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Age group not found with ID: " + id);
        }

        AgeGroupEntity updatedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
        updatedAgeGroup.setId(id);
        validateNonOverlappingAgeRange(updatedAgeGroup);
        return modelMapper.map(ageGroupRepository.save(updatedAgeGroup), AgeGroupDTO.AgeGroupResponseDTO.class);

    }

    @Override
    @Transactional
    public String deleteAgeGroup(Long id){
        AgeGroupEntity ageGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Age group not found with ID: " + id));

        if (ageGroup.getPersonList() != null && !ageGroup.getPersonList().isEmpty()) {
            throw new BusinessRuleException("Cannot delete age group because it has associated persons");
        }

        ageGroupRepository.deleteById(id);
        return "Age group deleted";
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgeGroupDTO.AgeGroupResponseDTO> findAllAgeGroup(){
        return ageGroupRepository.findAll()
                .stream()
                .map(ageGroupEntity -> modelMapper.map(ageGroupEntity, AgeGroupDTO.AgeGroupResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgeGroupDTO.AgeGroupResponseDTO> findAllByOrderByIdAsc(){
        return ageGroupRepository.findAllByOrderByIdAsc()
                .stream()
                .map(ageGroupEntity -> modelMapper.map(ageGroupEntity, AgeGroupDTO.AgeGroupResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AgeGroupDTO.AgeGroupResponseDTO findAgeGroupById(Long id){
        AgeGroupEntity ageGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Age group not found with ID: " + id));

        return modelMapper.map(ageGroup, AgeGroupDTO.AgeGroupResponseDTO.class);
    }

    private void validateNonOverlappingAgeRange(AgeGroupEntity ageGroup) {
        boolean existsOverlap = ageGroupRepository.existsOverlappingAgeRange(
                ageGroup.getMinAge(),
                ageGroup.getMaxAge(),
                ageGroup.getId()
        );

        if (existsOverlap) {
            throw new DuplicateResourceException("The age range (" + ageGroup.getMinAge() +
                    "-" + ageGroup.getMaxAge() +
                    ") overlaps with an existing age group");
        }
    }
}
