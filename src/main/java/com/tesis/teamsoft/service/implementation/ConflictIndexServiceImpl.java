package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.ConflictIndexEntity;
import com.tesis.teamsoft.persistence.repository.IConflictIndexRepository;
import com.tesis.teamsoft.presentation.dto.ConflictIndexDTO;
import com.tesis.teamsoft.service.interfaces.IConflictIndexService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConflictIndexServiceImpl implements IConflictIndexService {

    private final IConflictIndexRepository conflictIndexRepository;
    private final ModelMapper modelMapper;


    @Override
    public ConflictIndexDTO.ConflictIndexResponseDTO saveConflictIndex(ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO) {
        try {
            ConflictIndexEntity savedConflictIndex = modelMapper.map(conflictIndexDTO, ConflictIndexEntity.class);
            return modelMapper.map(conflictIndexRepository.save(savedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving conflict index: " + e.getMessage());
        }
    }

    @Override
    public ConflictIndexDTO.ConflictIndexResponseDTO updateConflictIndex(ConflictIndexDTO.ConflictIndexCreateDTO conflictIndexDTO, Long id) {

        if (!conflictIndexRepository.existsById(id)) {
            throw new RuntimeException("Conflict index not found with ID: " + id);
        }

        try {
            ConflictIndexEntity updatedConflictIndex = modelMapper.map(conflictIndexDTO, ConflictIndexEntity.class);
            updatedConflictIndex.setId(id);
            return modelMapper.map(conflictIndexRepository.save(updatedConflictIndex), ConflictIndexDTO.ConflictIndexResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating conflict index: " + e.getMessage());
        }
    }

    @Override
    public String deleteConflictIndex(Long id) {
        ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conflict index not found with ID: " + id));

        // Verificar si tiene PersonConflictEntity asociados antes de eliminar
        if (conflictIndex.getPersonConflictList() != null && !conflictIndex.getPersonConflictList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete conflict index because it has associated person conflicts");
        }

        conflictIndexRepository.deleteById(id);
        return "Conflict index deleted successfully";
    }

    @Override
    public List<ConflictIndexDTO.ConflictIndexResponseDTO> findAllConflictIndex() {
        try {
            return conflictIndexRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, ConflictIndexDTO.ConflictIndexResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all conflict indices: " + e.getMessage());
        }
    }

    @Override
    public List<ConflictIndexDTO.ConflictIndexResponseDTO> findAllByOrderByIdAsc() {
        try {
            return conflictIndexRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, ConflictIndexDTO.ConflictIndexResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all conflict indices: " + e.getMessage());
        }
    }

    @Override
    public ConflictIndexDTO.ConflictIndexResponseDTO findConflictIndexById(Long id) {
        ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conflict index not found with ID: " + id));

        return modelMapper.map(conflictIndex, ConflictIndexDTO.ConflictIndexResponseDTO.class);
    }
}