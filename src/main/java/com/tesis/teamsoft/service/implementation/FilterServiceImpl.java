package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.repository.IPersonRepository;
import com.tesis.teamsoft.presentation.dto.FilterDTO;
import com.tesis.teamsoft.presentation.dto.PersonDTO;
import com.tesis.teamsoft.service.interfaces.IFilterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements IFilterService {

    private final IPersonRepository personRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PersonDTO.PersonResponseDTO> filterPersons(FilterDTO.FilterRequestDTO filterDTO) {
        // Obtener lista de entidades
        List<PersonEntity> persons = personRepository.findByFilter(filterDTO);

        // Mapear a DTO
        return persons.stream()
                .map(person -> modelMapper.map(person, PersonDTO.PersonResponseDTO.class))
                .toList();
    }
}
