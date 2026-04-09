package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.FilterDTO;
import com.tesis.teamsoft.presentation.dto.PersonDTO;
import java.util.List;

public interface IFilterService {
    List<PersonDTO.PersonResponseDTO> filterPersons(FilterDTO.FilterRequestDTO filterDTO);
}