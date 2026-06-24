package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.CountyEntity;
import com.tesis.teamsoft.persistence.repository.ICountyRepository;
import com.tesis.teamsoft.presentation.dto.CountyDTO;
import com.tesis.teamsoft.service.interfaces.ICountyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountyServiceImpl implements ICountyService {

    private final ICountyRepository countyRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CountyDTO.CountyResponseDTO saveCounty(CountyDTO.CountyCreateDTO countyDTO) {
        CountyEntity savedCounty = modelMapper.map(countyDTO, CountyEntity.class);
        validateUniqueAttributes(countyDTO, null);
        return modelMapper.map(countyRepository.save(savedCounty), CountyDTO.CountyResponseDTO.class);
    }

    @Override
    @Transactional
    public CountyDTO.CountyResponseDTO updateCounty(CountyDTO.CountyCreateDTO countyDTO, Long id) {
        if (!countyRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_COUNTY_NOT_FOUND", id);
        }
        CountyEntity updatedCounty = modelMapper.map(countyDTO, CountyEntity.class);
        updatedCounty.setId(id);
        validateUniqueAttributes(countyDTO, id);
        return modelMapper.map(countyRepository.save(updatedCounty), CountyDTO.CountyResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteCounty(Long id) {
        CountyEntity county = countyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_COUNTY_NOT_FOUND", id));

        if (county.getProjectList() != null && !county.getProjectList().isEmpty()
                || county.getPersonList() != null && !county.getPersonList().isEmpty()
                || (county.getCostDistanceListA() != null && !county.getCostDistanceListA().isEmpty())
                || (county.getCostDistanceListB() != null && !county.getCostDistanceListB().isEmpty())) {
            throw new BusinessRuleException("ERR_COUNTY_CANT_BE_DELETED");
        }

        countyRepository.deleteById(id);
        return "COUNTY_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountyDTO.CountyResponseDTO> findAllCounty() {
        return countyRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, CountyDTO.CountyResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountyDTO.CountyResponseDTO> findAllByOrderByIdAsc() {
        return countyRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, CountyDTO.CountyResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CountyDTO.CountyResponseDTO findCountyById(Long id) {
        CountyEntity county = countyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_COUNTY_NOT_FOUND", id));
        return modelMapper.map(county, CountyDTO.CountyResponseDTO.class);
    }

    private void validateUniqueAttributes(CountyDTO.CountyCreateDTO dto, Long id) {
        boolean nameExists = (id == null) ?
                countyRepository.existsByCountyName(dto.getCountyName()) :
                countyRepository.existsByCountyNameAndIdNot(dto.getCountyName(), id);
        if (nameExists) {
            throw new DuplicateResourceException("ERR_COUNTY_NAME_ALREADY_EXISTS");
        }

        boolean codeExists = (id == null) ?
                countyRepository.existsByCode(dto.getCode()) :
                countyRepository.existsByCodeAndIdNot(dto.getCode(), id);
        if (codeExists) {
            throw new DuplicateResourceException("ERR_COUNTY_CODE_ALREADY_EXISTS");
        }
    }
}