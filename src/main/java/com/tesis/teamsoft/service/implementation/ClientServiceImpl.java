package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.ClientEntity;
import com.tesis.teamsoft.persistence.repository.IClientRepository;
import com.tesis.teamsoft.presentation.dto.ClientDTO;
import com.tesis.teamsoft.service.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ClientDTO.ClientResponseDTO saveClient(ClientDTO.ClientCreateDTO clientDTO) {
        ClientEntity savedClient = modelMapper.map(clientDTO, ClientEntity.class);
        return modelMapper.map(clientRepository.save(savedClient), ClientDTO.ClientResponseDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO.ClientResponseDTO updateClient(ClientDTO.ClientCreateDTO clientDTO, Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("ERR_CLIENT_NOT_FOUND", id);
        }

        ClientEntity updatedClient = modelMapper.map(clientDTO, ClientEntity.class);
        updatedClient.setId(id);
        return modelMapper.map(clientRepository.save(updatedClient), ClientDTO.ClientResponseDTO.class);
    }

    @Override
    @Transactional
    public String deleteClient(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_CLIENT_NOT_FOUND", id));

        if (client.getProjectList() != null && !client.getProjectList().isEmpty()) {
            throw new BusinessRuleException("ERR_CLIENT_CANT_BE_DELETED");
        }

        clientRepository.deleteById(id);
        return "CLIENT_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO.ClientResponseDTO> findAllClient() {
        return clientRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, ClientDTO.ClientResponseDTO.class))
                .toList();  // Java 16+ .toList() o Collectors.toList()
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO.ClientResponseDTO> findAllByOrderByIdAsc() {
        return clientRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entity -> modelMapper.map(entity, ClientDTO.ClientResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO.ClientResponseDTO findClientById(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_CLIENT_NOT_FOUND", id));
        return modelMapper.map(client, ClientDTO.ClientResponseDTO.class);
    }
}