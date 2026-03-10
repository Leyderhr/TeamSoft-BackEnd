package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.PersonGroupEntity;
import com.tesis.teamsoft.persistence.repository.IPersonGroupRepository;
import com.tesis.teamsoft.presentation.dto.PersonGroupDTO;
import com.tesis.teamsoft.service.interfaces.IPersonGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonGroupServiceImpl implements IPersonGroupService {

    private final IPersonGroupRepository personGroupRepository;
    private final ModelMapper modelMapper;


    @Override
    public PersonGroupDTO.PersonGroupResponseDTO savePersonGroup(PersonGroupDTO.PersonGroupCreateDTO personGroupDTO) {
        try {
            PersonGroupEntity personGroup = modelMapper.map(personGroupDTO, PersonGroupEntity.class);
            personGroup.setId(null);
            // Establecer el grupo padre si se proporciona
            if (personGroupDTO.getParentGroupId() != null) {
                PersonGroupEntity parentGroup = personGroupRepository.findById(personGroupDTO.getParentGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("Parent group not found with ID: " + personGroupDTO.getParentGroupId()));

                // Validar que no se cree una referencia circular
                validateNoCircularReference(parentGroup, null);
                personGroup.setParentGroup(parentGroup);
            }

            return convertToResponseDTO(personGroupRepository.save(personGroup));
            //return modelMapper.map(personGroupRepository.save(personGroup), PersonGroupDTO.PersonGroupResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving person group: " + e.getMessage());
        }
    }

    @Override
    public PersonGroupDTO.PersonGroupResponseDTO updatePersonGroup(PersonGroupDTO.PersonGroupCreateDTO personGroupDTO, Long id) {

        if (!personGroupRepository.existsById(id)) {
            throw new RuntimeException("Person group not found with ID: " + id);
        }

        try {
            PersonGroupEntity updatedPersonGroup = modelMapper.map(personGroupDTO, PersonGroupEntity.class);
            updatedPersonGroup.setId(id);
            // Establecer el grupo padre si se proporciona
            if (personGroupDTO.getParentGroupId() != null) {
                PersonGroupEntity parentGroup = personGroupRepository.findById(personGroupDTO.getParentGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("Parent group not found with ID: " + personGroupDTO.getParentGroupId()));

                // Validar que no se cree una referencia circular
                validateNoCircularReference(parentGroup, id);
                updatedPersonGroup.setParentGroup(parentGroup);
            } else {
                updatedPersonGroup.setParentGroup(null);
            }

            return modelMapper.map(personGroupRepository.save(updatedPersonGroup), PersonGroupDTO.PersonGroupResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating person group: " + e.getMessage());
        }
    }

    @Override
    public String deletePersonGroup(Long id) {
        PersonGroupEntity personGroup = personGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person group not found with ID: " + id));

        // Verificar si tiene personas asociadas
        if (personGroup.getPersonList() != null && !personGroup.getPersonList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete person group because it has associated persons");
        }

        // Verificar si tiene subgrupos asociados
        if (personGroup.getPersonGroupList() != null && !personGroup.getPersonGroupList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete person group because it has associated child groups");
        }

        personGroupRepository.deleteById(id);
        return "Person group deleted successfully";
    }

    @Override
    public List<PersonGroupDTO.PersonGroupResponseDTO> findAllPersonGroup() {
        try {
            return personGroupRepository.findAll()
                    .stream()
                    .map(personGroupEntity -> modelMapper.map(personGroupEntity, PersonGroupDTO.PersonGroupResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all person groups: " + e.getMessage());
        }
    }

    @Override
    public List<PersonGroupDTO.PersonGroupResponseDTO> findAllByOrderByIdAsc() {
        try {
            return personGroupRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(personGroupEntity -> modelMapper.map(personGroupEntity, PersonGroupDTO.PersonGroupResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all person groups ordered by ID: " + e.getMessage());
        }
    }

    @Override
    public PersonGroupDTO.PersonGroupResponseDTO findPersonGroupById(Long id) {
        PersonGroupEntity personGroup = personGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person group not found with ID: " + id));

        return modelMapper.map(personGroup, PersonGroupDTO.PersonGroupResponseDTO.class);
    }

    private void validateNoCircularReference(PersonGroupEntity parentGroup, Long currentGroupId) {
        // Verificar que no se esté asignando un hijo como padre (referencia circular)
        PersonGroupEntity current = parentGroup;
        while (current != null) {
            if (current.getId().equals(currentGroupId)) {
                throw new IllegalArgumentException("Circular reference detected: cannot assign a child group as parent");
            }
            current = current.getParentGroup();
        }
    }

    private PersonGroupDTO.PersonGroupResponseDTO convertToResponseDTO(PersonGroupEntity personGroupEntity) {
        PersonGroupDTO.PersonGroupResponseDTO dto = new PersonGroupDTO.PersonGroupResponseDTO();

        dto = modelMapper.map(personGroupEntity, PersonGroupDTO.PersonGroupResponseDTO.class);
        dto.setFather(personGroupEntity.getParentGroup().getName());

        return dto;
    }
}