package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import microservice.client_service.Mappers.ClientMapper;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Client client = clientMapper.insertDtoToEntity(clientInsertDTO);
            client = clientRepository.saveAndFlush(client);
            return clientMapper.entityToDTO(client);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<ClientDTO> getClientById(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Client> client = clientRepository.findById(clientId);
            return client.map(clientMapper::entityToDTO).orElse(null);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<ClientDTO>> getAllClients() {
        return CompletableFuture.supplyAsync(() -> {
            List<Client> clients = clientRepository.findAll();
            return clients.stream()
                    .map(clientMapper::entityToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> deleteClient(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return false;
            }
            clientRepository.deleteById(clientId);
            return true;
        });
    }
}