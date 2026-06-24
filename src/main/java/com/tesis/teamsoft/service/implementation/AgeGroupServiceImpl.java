package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.AgeGroupEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.repository.IAgeGroupRepository;
import com.tesis.teamsoft.persistence.repository.IPersonRepository;
import com.tesis.teamsoft.presentation.dto.AgeGroupDTO;
import com.tesis.teamsoft.service.interfaces.IAgeGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgeGroupServiceImpl implements IAgeGroupService {


    private final IAgeGroupRepository ageGroupRepository;
    private final IPersonRepository personRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public AgeGroupDTO.AgeGroupResponseDTO saveAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO) {
        AgeGroupEntity savedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
        validateUniqueAgeGroupName(savedAgeGroup, null);
        validateNonOverlappingAgeRange(savedAgeGroup);

        return modelMapper.map(ageGroupRepository.save(savedAgeGroup), AgeGroupDTO.AgeGroupResponseDTO.class);
    }

    @Override
    @Transactional
    public AgeGroupDTO.AgeGroupResponseDTO updateAgeGroup(AgeGroupDTO.AgeGroupCreateDTO ageGroupDTO, Long id){
        AgeGroupEntity updatedAgeGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_AGE_GROUP_NOT_FOUND", id));

        boolean isSameAgeRange = checkAgeRangeChange(updatedAgeGroup, ageGroupDTO.getMinAge(), ageGroupDTO.getMaxAge());

        updatedAgeGroup = modelMapper.map(ageGroupDTO, AgeGroupEntity.class);
        updatedAgeGroup.setId(id);
        validateUniqueAgeGroupName(updatedAgeGroup, id);
        validateNonOverlappingAgeRange(updatedAgeGroup);
        if(!isSameAgeRange) reassignAgeGroupForUpdate(updatedAgeGroup);

        return modelMapper.map(ageGroupRepository.save(updatedAgeGroup), AgeGroupDTO.AgeGroupResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteAgeGroup(Long id){
        AgeGroupEntity ageGroup = ageGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_AGE_GROUP_NOT_FOUND", id));

        if (ageGroup.getPersonList() != null && !ageGroup.getPersonList().isEmpty()) {
            throw new BusinessRuleException("ERR_AGE_GROUP_CANT_BE_DELETED");
        }

        ageGroupRepository.deleteById(id);
        return "AGE_GROUP_SUCCESSFULLY_DELETED";
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
                .orElseThrow(() -> new ResourceNotFoundException("ERR_AGE_GROUP_NOT_FOUND", id));

        return modelMapper.map(ageGroup, AgeGroupDTO.AgeGroupResponseDTO.class);
    }

    private void validateNonOverlappingAgeRange(AgeGroupEntity ageGroup) {
        boolean existsOverlap = ageGroupRepository.existsOverlappingAgeRange(
                ageGroup.getMinAge(),
                ageGroup.getMaxAge(),
                ageGroup.getId()
        );

        if (existsOverlap) {
            throw new DuplicateResourceException("ERR_AGE_GROUP_OVERLAP");
        }
    }

    private void reassignAgeGroupForUpdate(AgeGroupEntity updatedGroup) {
        LocalDate today = LocalDate.now();

        LocalDate newMinBirthDate = today.minusYears(updatedGroup.getMaxAge());
        LocalDate newMaxBirthDate = today.minusYears(updatedGroup.getMinAge());
        Date newStartDate = Date.from(newMinBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date newEndDate = Date.from(newMaxBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<PersonEntity> personsToRemove = personRepository.findByAgeGroupIdAndBirthDateNotBetween(
                updatedGroup.getId(), newStartDate, newEndDate);

        if (!personsToRemove.isEmpty()) {
            personsToRemove.forEach(person -> person.setAgeGroup(null));
            personRepository.saveAll(personsToRemove);
        }

        List<PersonEntity> personsToAdd = personRepository.findByAgeGroupIsNullAndBirthDateBetween(newStartDate, newEndDate);
        if (!personsToAdd.isEmpty()) {
            personsToAdd.forEach(person -> person.setAgeGroup(updatedGroup));
            personRepository.saveAll(personsToAdd);
        }
    }

    private boolean checkAgeRangeChange(AgeGroupEntity updatedAgeGroup, int newMinAge, int newMaxAge){
        return updatedAgeGroup.getMinAge() == newMinAge && updatedAgeGroup.getMaxAge() == newMaxAge;
    }

    private void validateUniqueAgeGroupName(AgeGroupEntity entity, Long id) {
        boolean existName = (id == null) ?
                ageGroupRepository.existsByAgeGroupName(entity.getAgeGroupName()) :
                ageGroupRepository.existsByAgeGroupNameAndIdNot(entity.getAgeGroupName(), id);
        if (existName) {
            throw new DuplicateResourceException("ERR_AGE_GROUP_NAME_DUPLICATE");
        }
    }
}
