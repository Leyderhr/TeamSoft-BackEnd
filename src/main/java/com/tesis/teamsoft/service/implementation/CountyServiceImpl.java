package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountyServiceImpl implements ICountyService {

    private final ICountyRepository countyRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CountyDTO.CountyResponseDTO saveCounty(CountyDTO.CountyCreateDTO countyDTO) {
        CountyEntity savedCounty = modelMapper.map(countyDTO, CountyEntity.class);
        return modelMapper.map(countyRepository.save(savedCounty), CountyDTO.CountyResponseDTO.class);
    }

    @Override
    @Transactional
    public CountyDTO.CountyResponseDTO updateCounty(CountyDTO.CountyCreateDTO countyDTO, Long id) {
        if (!countyRepository.existsById(id)) {
            throw new ResourceNotFoundException("County not found with ID: " + id);
        }
        CountyEntity updatedCounty = modelMapper.map(countyDTO, CountyEntity.class);
        updatedCounty.setId(id);
        return modelMapper.map(countyRepository.save(updatedCounty), CountyDTO.CountyResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteCounty(Long id) {
        CountyEntity county = countyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("County not found with ID: " + id));

        StringBuilder errorMessage = canByDeleted(county);
        if (!errorMessage.isEmpty()) {
            throw new BusinessRuleException(errorMessage.toString().trim());
        }

        countyRepository.deleteById(id);
        return "County deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("County not found with ID: " + id));
        return modelMapper.map(county, CountyDTO.CountyResponseDTO.class);
    }

    private StringBuilder canByDeleted(CountyEntity county){
        StringBuilder errorMessage = new StringBuilder();

        if (county.getProjectList() != null && !county.getProjectList().isEmpty()) {
            errorMessage.append("Cannot delete county because it has associated projects. ");
        }

        if (county.getPersonList() != null && !county.getPersonList().isEmpty()) {
            errorMessage.append("Cannot delete county because it has associated persons. ");
        }

        if ((county.getCostDistanceListA() != null && !county.getCostDistanceListA().isEmpty())
                || (county.getCostDistanceListB() != null && !county.getCostDistanceListB().isEmpty())) {
            errorMessage.append("Cannot delete county because it has associated cost distances");
        }

        return errorMessage;
    }
}