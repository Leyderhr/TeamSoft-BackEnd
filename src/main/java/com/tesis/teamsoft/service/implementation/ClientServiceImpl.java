package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.ClientEntity;
import com.tesis.teamsoft.persistence.repository.IClientRepository;
import com.tesis.teamsoft.presentation.dto.ClientDTO;
import com.tesis.teamsoft.service.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ModelMapper modelMapper;


    @Override
    public ClientDTO.ClientResponseDTO saveClient(ClientDTO.ClientCreateDTO clientDTO) {
        try {
            ClientEntity savedClient = modelMapper.map(clientDTO, ClientEntity.class);
            return modelMapper.map(clientRepository.save(savedClient), ClientDTO.ClientResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving client: " + e.getMessage());
        }
    }

    @Override
    public ClientDTO.ClientResponseDTO updateClient(ClientDTO.ClientCreateDTO clientDTO, Long id) {

        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found with ID: " + id);
        }

        try {
            ClientEntity updatedClient = modelMapper.map(clientDTO, ClientEntity.class);
            updatedClient.setId(id);
            clientRepository.save(updatedClient);
            return modelMapper.map(updatedClient, ClientDTO.ClientResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating client: " + e.getMessage());
        }
    }

    @Override
    public String deleteClient(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        // Verificar si tiene proyectos asociados antes de eliminar
        if (client.getProjectList() != null && !client.getProjectList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete client because it has associated projects");
        }

        clientRepository.deleteById(id);
        return "Client deleted successfully";
    }

    @Override
    public List<ClientDTO.ClientResponseDTO> findAllClient() {
        try {
            return clientRepository.findAll()
                    .stream()
                    .map(entity -> modelMapper.map(entity, ClientDTO.ClientResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all clients: " + e.getMessage());
        }
    }

    @Override
    public List<ClientDTO.ClientResponseDTO> findAllByOrderByIdAsc() {
        try {
            return clientRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(entity -> modelMapper.map(entity, ClientDTO.ClientResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all clients: " + e.getMessage());
        }
    }

    @Override
    public ClientDTO.ClientResponseDTO findClientById(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        return modelMapper.map(client, ClientDTO.ClientResponseDTO.class);
    }
}