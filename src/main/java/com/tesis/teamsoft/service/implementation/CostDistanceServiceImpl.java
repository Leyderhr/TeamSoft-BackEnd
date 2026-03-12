package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.CostDistanceEntity;
import com.tesis.teamsoft.persistence.entity.CountyEntity;
import com.tesis.teamsoft.persistence.repository.ICostDistanceRepository;
import com.tesis.teamsoft.persistence.repository.ICountyRepository;
import com.tesis.teamsoft.presentation.dto.CostDistanceDTO;
import com.tesis.teamsoft.presentation.dto.CountyDTO;
import com.tesis.teamsoft.service.interfaces.ICostDistanceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostDistanceServiceImpl implements ICostDistanceService {

    private final ICostDistanceRepository costDistanceRepository;
    private final ICountyRepository countyRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CostDistanceDTO.CostDistanceResponseDTO saveCostDistance(CostDistanceDTO.CostDistanceCreateDTO costDistanceDTO) {
        CostDistanceEntity savedCostDistance = initializeCostDistance(costDistanceDTO);
        validateByCountyPairExcludingId(savedCostDistance);
        costDistanceRepository.save(savedCostDistance);
        return convertToResponseDTO(savedCostDistance);
    }

    @Override
    @Transactional
    public CostDistanceDTO.CostDistanceResponseDTO updateCostDistance(CostDistanceDTO.CostDistanceCreateDTO costDistanceDTO, Long id) {
        if (!costDistanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cost distance not found with ID: " + id);
        }

        CostDistanceEntity updatedCostDistance = initializeCostDistance(costDistanceDTO);
        updatedCostDistance.setId(id);
        validateByCountyPairExcludingId(updatedCostDistance);
        costDistanceRepository.save(updatedCostDistance);
        return convertToResponseDTO(updatedCostDistance);
    }

    @Override
    @Transactional
    public String deleteCostDistance(Long id) {
        if (!costDistanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cost distance not found with ID: " + id);
        }
        costDistanceRepository.deleteById(id);
        return "Cost distance deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<CostDistanceDTO.CostDistanceResponseDTO> findAllCostDistance() {
        return costDistanceRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CostDistanceDTO.CostDistanceResponseDTO> findAllByOrderByIdAsc() {
        return costDistanceRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CostDistanceDTO.CostDistanceResponseDTO findCostDistanceById(Long id) {
        CostDistanceEntity costDistance = costDistanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cost distance not found with ID: " + id));
        return convertToResponseDTO(costDistance);
    }


    private CostDistanceEntity initializeCostDistance(CostDistanceDTO.CostDistanceCreateDTO costDistanceDTO) {
        CountyEntity countyA = countyRepository.findById(costDistanceDTO.getCountyAId())
                .orElseThrow(() -> new ResourceNotFoundException("County A not found with ID: " + costDistanceDTO.getCountyAId()));

        CountyEntity countyB = countyRepository.findById(costDistanceDTO.getCountyBId())
                .orElseThrow(() -> new ResourceNotFoundException("County B not found with ID: " + costDistanceDTO.getCountyBId()));

        if (countyA.getId().equals(countyB.getId())) {
            throw new BusinessRuleException("County A and County B cannot be the same");
        }

        return new CostDistanceEntity(null, costDistanceDTO.getCostDistance(), countyA, countyB);
    }

    private void validateByCountyPairExcludingId(CostDistanceEntity costDistance) {
        boolean exist = costDistanceRepository.existsByCountyPairExcludingId(
                costDistance.getCountyA().getId(),
                costDistance.getCountyB().getId(),
                costDistance.getId()
        );

        if (exist) {
            throw new BusinessRuleException("Cost distance already exists between these counties");
        }
    }
    
    private CostDistanceDTO.CostDistanceResponseDTO convertToResponseDTO(CostDistanceEntity entity) {
        CostDistanceDTO.CostDistanceResponseDTO dto = new CostDistanceDTO.CostDistanceResponseDTO();
        dto.setId(entity.getId());
        dto.setCostDistance(entity.getCostDistance());

        dto.setCountyA(modelMapper.map(entity.getCountyA(), CountyDTO.CountyResponseDTO.class));
        dto.setCountyB(modelMapper.map(entity.getCountyB(), CountyDTO.CountyResponseDTO.class));

        return dto;
    }
}