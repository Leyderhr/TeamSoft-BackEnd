package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.CountyEntity;
import com.tesis.teamsoft.persistence.repository.ICountyRepository;
import com.tesis.teamsoft.presentation.dto.CountyDTO;
import com.tesis.teamsoft.service.interfaces.ICountyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountyServiceImpl implements ICountyService {

    private final ICountyRepository countyRepository;
    private final ModelMapper modelMapper;

    @Override
    public CountyDTO.CountyResponseDTO saveCounty(CountyDTO.CountyCreateDTO countyDTO) {
        try {
            CountyEntity savedCounty = modelMapper.map(countyDTO, CountyEntity.class);
            return modelMapper.map(countyRepository.save(savedCounty), CountyDTO.CountyResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving county: " + e.getMessage());
        }
    }

    @Override
    public CountyDTO.CountyResponseDTO updateCounty(CountyDTO.CountyCreateDTO countyDTO, Long id) {

        if (!countyRepository.existsById(id)) {
            throw new RuntimeException("County not found with ID: " + id);
        }

        try {
            CountyEntity updatedCounty = modelMapper.map(countyDTO, CountyEntity.class);
            updatedCounty.setId(id);
            return modelMapper.map(countyRepository.save(updatedCounty), CountyDTO.CountyResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating county: " + e.getMessage());
        }
    }

    @Override
    public String deleteCounty(Long id) {
        CountyEntity county = countyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("County not found with ID: " + id));

        StringBuilder errorMessage = canByDeleted(county);

        if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage.toString().trim());
        }

        countyRepository.deleteById(id);
        return "County deleted successfully";
    }

    @Override
    public List<CountyDTO.CountyResponseDTO> findAllCounty() {
        try {
            return countyRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, CountyDTO.CountyResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all counties: " + e.getMessage());
        }
    }

    @Override
    public List<CountyDTO.CountyResponseDTO> findAllByOrderByIdAsc() {
        try {
            return countyRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, CountyDTO.CountyResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all counties: " + e.getMessage());
        }
    }

    @Override
    public CountyDTO.CountyResponseDTO findCountyById(Long id) {
        CountyEntity county = countyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("County not found with ID: " + id));

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