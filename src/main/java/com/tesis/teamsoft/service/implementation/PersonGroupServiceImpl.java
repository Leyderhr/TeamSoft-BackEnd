package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.PersonGroupEntity;
import com.tesis.teamsoft.persistence.repository.IPersonGroupRepository;
import com.tesis.teamsoft.presentation.dto.PersonGroupDTO;
import com.tesis.teamsoft.service.interfaces.IPersonGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonGroupServiceImpl implements IPersonGroupService {

    private final IPersonGroupRepository personGroupRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public PersonGroupDTO.PersonGroupResponseDTO savePersonGroup(PersonGroupDTO.PersonGroupCreateDTO personGroupDTO) {
        PersonGroupEntity personGroup = modelMapper.map(personGroupDTO, PersonGroupEntity.class);
        personGroup.setId(null);

        if (personGroupDTO.getParentGroupId() != null) {
            PersonGroupEntity parentGroup = personGroupRepository.findById(personGroupDTO.getParentGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_PARENT_GROUP_NOT_FOUND", personGroupDTO.getParentGroupId()));

            validateNoCircularReference(parentGroup, null);
            personGroup.setParentGroup(parentGroup);
        }

        return convertToResponseDTO(personGroupRepository.save(personGroup));
    }

    @Override
    @Transactional
    public PersonGroupDTO.PersonGroupResponseDTO updatePersonGroup(PersonGroupDTO.PersonGroupCreateDTO personGroupDTO, Long id) {
        if (!personGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_PERSON_GROUP_NOT_FOUND", id);
        }
        PersonGroupEntity updatedPersonGroup = modelMapper.map(personGroupDTO, PersonGroupEntity.class);
        updatedPersonGroup.setId(id);

        if (personGroupDTO.getParentGroupId() != null) {
            PersonGroupEntity parentGroup = personGroupRepository.findById(personGroupDTO.getParentGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_PARENT_GROUP_NOT_FOUND", personGroupDTO.getParentGroupId()));

            validateNoCircularReference(parentGroup, id);
            updatedPersonGroup.setParentGroup(parentGroup);
        } else {
            updatedPersonGroup.setParentGroup(null);
        }

        return convertToResponseDTO(personGroupRepository.save(updatedPersonGroup));
    }

    @Override
    @Transactional
    public String deletePersonGroup(Long id) {
        PersonGroupEntity personGroup = personGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_GROUP_NOT_FOUND", id));

        if (personGroup.getPersonList() != null && !personGroup.getPersonList().isEmpty())
            throw new BusinessRuleException("ERR_PERSON_GROUP_CANT_BE_DELETED");

        if (personGroup.getPersonGroupList() != null && !personGroup.getPersonGroupList().isEmpty())
            throw new BusinessRuleException("ERR_PERSON_GROUP_CANT_BE_DELETED");

        personGroupRepository.deleteById(id);
        return "PERSON_GROUP_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonGroupDTO.PersonGroupResponseDTO> findAllPersonGroup() {
        return personGroupRepository.findAll()
                .stream()
                .map(personGroupEntity -> modelMapper.map(personGroupEntity, PersonGroupDTO.PersonGroupResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonGroupDTO.PersonGroupResponseDTO> findAllByOrderByIdAsc() {
        return personGroupRepository.findAllByOrderByIdAsc()
                .stream()
                .map(personGroupEntity -> convertToResponseDTO(personGroupRepository.save(personGroupEntity)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonGroupDTO.PersonGroupResponseDTO findPersonGroupById(Long id) {
        PersonGroupEntity personGroup = personGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_GROUP_NOT_FOUND", id));
        return convertToResponseDTO(personGroupRepository.save(personGroup));
    }

    private void validateNoCircularReference(PersonGroupEntity parentGroup, Long currentGroupId) {
        // Verificar que no se esté asignando un hijo como padre (referencia circular)
        PersonGroupEntity current = parentGroup;
        while (current != null) {
            if (current.getId().equals(currentGroupId)) {
                throw new BusinessRuleException("ERR_PERSON_GROUP_CIRCULAR_REFERENCE");
            }
            current = current.getParentGroup();
        }
    }

    private PersonGroupDTO.PersonGroupResponseDTO convertToResponseDTO(PersonGroupEntity personGroupEntity) {
        PersonGroupDTO.PersonGroupResponseDTO dto;

        dto = modelMapper.map(personGroupEntity, PersonGroupDTO.PersonGroupResponseDTO.class);

        if(personGroupEntity.getParentGroup() != null)
            dto.setFather(personGroupEntity.getParentGroup().getName());

        return dto;
    }
}