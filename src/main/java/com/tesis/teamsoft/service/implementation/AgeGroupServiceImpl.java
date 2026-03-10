package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.AgeGroupEntity;
import com.tesis.teamsoft.persistence.repository.IAgeGroupRepository;
import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.service.interfaces.IAgeGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgeGroupServiceImpl implements IAgeGroupService {


    private final IAgeGroupRepository ageGroupRepository;
    private final ModelMapper modelMapper;


    @Override
    public AgeGroupDTO.AgeGroupResponseDTO saveAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO){
        try {
            AgeGroupEntity savedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
            validateNonOverlappingAgeRange(savedAgeGroup);
            return modelMapper.map(ageGroupRepository.save(savedAgeGroup), AgeGroupDTO.AgeGroupResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving age group: " + e.getMessage());
        }
    }

    @Override
    public AgeGroupDTO.AgeGroupResponseDTO updateAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO, Long id){

        if(!ageGroupRepository.existsById(id)){
            throw new RuntimeException("Age group not found with ID: " + id);
        }

        try {
            AgeGroupEntity updatedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
            updatedAgeGroup.setId(id);
            validateNonOverlappingAgeRange(updatedAgeGroup);
            ageGroupRepository.save(updatedAgeGroup);
            return modelMapper.map(updatedAgeGroup, AgeGroupDTO.AgeGroupResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating age group" + e.getMessage());
        }
    }

    @Override
    public String deleteAgeGroup(Long id){
        AgeGroupEntity ageGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Age group not found with ID: " + id));

        // Verificar si tiene personas asociadas antes de eliminar
        if (ageGroup.getPersonList() != null && !ageGroup.getPersonList().isEmpty()) {
            throw new IllegalArgumentException("Can't delete this age group");
        }

        ageGroupRepository.deleteById(id);
        return "Age group deleted";
    }

    @Override
    public List<AgeGroupDTO.AgeGroupResponseDTO> findAllAgeGroup(){
        try {
            return ageGroupRepository.findAll()
                    .stream()
                    .map(ageGroupEntity -> modelMapper.map(ageGroupEntity, AgeGroupDTO.AgeGroupResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all age groups " + e.getMessage());
        }
    }

    @Override
    public List<AgeGroupDTO.AgeGroupResponseDTO> findAllByOrderByIdAsc(){
        try {
            return ageGroupRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(ageGroupEntity -> modelMapper.map(ageGroupEntity, AgeGroupDTO.AgeGroupResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all age groups " + e.getMessage());
        }
    }

    @Override
    public AgeGroupDTO.AgeGroupResponseDTO findAgeGroupById(Long id){
        AgeGroupEntity ageGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Age group not found with ID: " + id));

        return modelMapper.map(ageGroup, AgeGroupDTO.AgeGroupResponseDTO.class);
    }

    private void validateNonOverlappingAgeRange(AgeGroupEntity ageGroup) {
        boolean existsOverlap = ageGroupRepository.existsOverlappingAgeRange(
                ageGroup.getMinAge(),
                ageGroup.getMaxAge(),
                ageGroup.getId()
        );

        if (existsOverlap) {
            throw new IllegalArgumentException("The age range (" + ageGroup.getMinAge() +
                    "-" + ageGroup.getMaxAge() +
                    ") overlaps with an existing age group");
        }
    }
}
