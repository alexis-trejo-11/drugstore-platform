package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.client_service.Mappers.ClientMapper;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.ClientRepository;
import microservice.client_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Async
    @Transactional
    public ClientDTO CreateClient(ClientInsertDTO clientInsertDTO) {
            Client client = clientMapper.insertDtoToEntity(clientInsertDTO);

            clientRepository.saveAndFlush(client);

            return clientMapper.entityToDTO(client);
    }

    @Async
    public ClientDTO getClientById(Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            return null;
        }
        return clientMapper.entityToDTO(client.get());
    }

    @Async
    public List<ClientDTO>  getAllClients() {
        List<Client> client = clientRepository.findAll();

        return client.stream()
                .map(clientMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Async
    @Transactional
    public boolean deleteClient(Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            return false;
        }

        clientRepository.deleteById(clientId);

        return true;
    }
}
