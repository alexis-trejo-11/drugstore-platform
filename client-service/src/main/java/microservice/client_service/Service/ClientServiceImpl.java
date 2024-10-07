package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.EntityMapper;
import jakarta.persistence.EntityNotFoundException;
import microservice.client_service.Mappers.ClientMapper;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public Optional<ClientDTO> getClientById(Long clientId) {
            Optional<Client> client = clientRepository.findById(clientId);
            return client.map(clientMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "clientsCache")
    public Page<ClientDTO> getClientsSortedByName(Pageable pageable) {
        Page<Client> clients = clientRepository.findAllByOrderByLastNameAscFirstNameAsc(pageable);
        return clients.map(clientMapper::entityToDTO);
    }

    @Override
    @Transactional
    public ClientDTO createClient(ClientInsertDTO clientInsertDTO) {
        Client client = clientMapper.insertDtoToEntity(clientInsertDTO);
        client = clientRepository.saveAndFlush(client);
        return clientMapper.entityToDTO(client);
    }

    @Override
    @Transactional
    public void updateClient(ClientUpdateDTO clientUpdateDTO) {
        Client client = clientRepository.findById(clientUpdateDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client with id " + clientUpdateDTO.getId() + " not found"));

        clientMapper.updateClientFromDto(clientUpdateDTO, client);
        clientRepository.saveAndFlush(client);
    }

    @Override
    @Transactional
    public void deleteClientByID(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client with id " + clientId + " not found");
        }

        clientRepository.deleteById(clientId);
    }

    @Override
    public boolean validateExistingClient(Long clientID) {
        return clientRepository.findById(clientID).isPresent();
    }

    @Override
    public void adjustLoyaltyPoints(Long clientId, int points) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client with id " + clientId + " not found"));

        if (points > 0) {
            client.addLoyaltyPoints(points);
        } else {
          client.deductLoyaltyPoints(-points);
        }

        clientRepository.saveAndFlush(client);
    }
}