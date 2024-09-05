package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface ClientService {
    CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    CompletableFuture<ClientDTO> getClientById(Long clientId);
    Page<ClientDTO> getClientsSortedByName(Pageable pageable);
    void deleteClientByID(Long clientId);
    void updateClient(ClientUpdateDTO clientUpdateDTO);
    boolean validateExistingClient(Long clientID);
}
