package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.DuplicateResourceException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.ConflictIndexEntity;
import com.tesis.teamsoft.persistence.repository.IConflictIndexRepository;
import com.tesis.teamsoft.presentation.dto.ConflictIndexDTO;
import com.tesis.teamsoft.service.interfaces.IConflictIndexService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConflictIndexServiceImpl implements IConflictIndexService {

    private final IConflictIndexRepository conflictIndexRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ConflictIndexDTO.ConflictIndexResponseDTO saveConflictIndex(ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO) {
        ConflictIndexEntity savedConflictIndex = modelMapper.map(conflictIndexDTO, ConflictIndexEntity.class);
        validateUniqueAttributes(conflictIndexDTO, null);
        return modelMapper.map(conflictIndexRepository.save(savedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }

    @Override
    @Transactional
    public ConflictIndexDTO.ConflictIndexResponseDTO updateConflictIndex(ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO, Long id) {
        if (!conflictIndexRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_CONFLICT_INDEX_NOT_FOUND", id);
        }

        ConflictIndexEntity updatedConflictIndex = modelMapper.map(conflictIndexDTO, ConflictIndexEntity.class);
        updatedConflictIndex.setId(id);
        validateUniqueAttributes(conflictIndexDTO, id);
        return modelMapper.map(conflictIndexRepository.save(updatedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteConflictIndex(Long id) {
        ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_CONFLICT_INDEX_NOT_FOUND", id));

        if (conflictIndex.getPersonConflictList() != null && !conflictIndex.getPersonConflictList().isEmpty()) {
            throw new BusinessRuleException("ERR_CONFLICT_INDEX_CANT_BE_DELETED");
        }

        conflictIndexRepository.deleteById(id);
        return "CONFLICT_INDEX_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConflictIndexDTO.ConflictIndexResponseDTO> findAllConflictIndex() {
        return conflictIndexRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, ConflictIndexDTO.ConflictIndexResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConflictIndexDTO.ConflictIndexResponseDTO> findAllByOrderByIdAsc() {
        return conflictIndexRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, ConflictIndexDTO.ConflictIndexResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConflictIndexDTO.ConflictIndexResponseDTO findConflictIndexById(Long id) {
        ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_CONFLICT_INDEX_NOT_FOUND", id));
        return modelMapper.map(conflictIndex, ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }

    private void validateUniqueAttributes(ConflictIndexDTO.ConflictIndexCreateDTO dto, Long id) {
        boolean descriptionExists = (id == null) ?
                conflictIndexRepository.existsByDescription(dto.getDescription()) :
                conflictIndexRepository.existsByDescriptionAndIdNot(dto.getDescription(), id);

        if (descriptionExists) {
            throw new DuplicateResourceException("ERR_CONFLICT_INDEX_DESCRIPTION_ALREADY_EXISTS");
        }

        boolean weightExists = (id == null) ?
                conflictIndexRepository.existsByWeight(dto.getWeight()) :
                conflictIndexRepository.existsByWeightAndIdNot(dto.getWeight(), id);

        if (weightExists) {
            throw new DuplicateResourceException("ERR_CONFLICT_INDEX_WEIGHT_ALREADY_EXISTS");
        }
    }
}