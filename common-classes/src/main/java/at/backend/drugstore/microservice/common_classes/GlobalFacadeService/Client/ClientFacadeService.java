package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ClientFacadeService {
    CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    CompletableFuture<ClientDTO> getClientById(Long clientId);
    boolean validateExistingClient(Long clientId);
}
