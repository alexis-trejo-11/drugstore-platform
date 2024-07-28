package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ClientService {
    CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    CompletableFuture<ClientDTO> getClientById(Long clientId);
    CompletableFuture<List<ClientDTO>> getAllClients();
    CompletableFuture<Boolean> deleteClient(Long clientId);
}
