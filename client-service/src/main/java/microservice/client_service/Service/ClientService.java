package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.ClientRepository;
import microservice.client_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Async
    @Transactional
    public ClientDTO CreateClient(ClientInsertDTO clientInsertDTO) {
        try {
            Client client = ModelTransformer.insertDtoToClient(clientInsertDTO);

            clientRepository.saveAndFlush(client);

            ClientDTO clientDTO = ModelTransformer.clientToReturnDTO(client);
            return clientDTO;
        } catch (Exception e) {
            throw new RuntimeException("An Error Occurred Creating Client");
        }
    }

    @Async
    public CompletableFuture<Result<ClientDTO>> getClientById(Long clientId) {
        try {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Client With Id: " + clientId + " Not Found"));
            } else {
                ClientDTO clientDTO = ModelTransformer.clientToReturnDTO(client.get());
                return CompletableFuture.completedFuture(Result.success(clientDTO));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Finding Client"));
        }

    }

    @Async
    public CompletableFuture<List<ClientDTO>>  getAllClients() {
        try {
            List<Client> client = clientRepository.findAll();

            List<ClientDTO> clientDTOS = client.stream()
                    .map(ModelTransformer::clientToReturnDTO)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(clientDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Finding Clients"));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<?>> deleteClient(Long clientId) {
        try {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Client With Id: " + clientId + " Not Found"));
            } else {
                clientRepository.deleteById(clientId);
                return CompletableFuture.completedFuture(Result.success("Client With Id: " + clientId + " Deleted"));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Deleting Client"));
        }

    }
}
