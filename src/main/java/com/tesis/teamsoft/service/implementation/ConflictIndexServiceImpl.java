package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
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
        return modelMapper.map(conflictIndexRepository.save(savedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }

    @Override
    @Transactional
    public ConflictIndexDTO.ConflictIndexResponseDTO updateConflictIndex(ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO, Long id) {
        if (!conflictIndexRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conflict index not found with ID: " + id);
        }

        ConflictIndexEntity updatedConflictIndex = modelMapper.map(conflictIndexDTO, ConflictIndexEntity.class);
        updatedConflictIndex.setId(id);
        return modelMapper.map(conflictIndexRepository.save(updatedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteConflictIndex(Long id) {
        ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conflict index not found with ID: " + id));

        if (conflictIndex.getPersonConflictList() != null && !conflictIndex.getPersonConflictList().isEmpty()) {
            throw new BusinessRuleException("Cannot delete conflict index because it has associated person conflicts");
        }

        conflictIndexRepository.deleteById(id);
        return "Conflict index deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("Conflict index not found with ID: " + id));
        return modelMapper.map(conflictIndex, ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }
}