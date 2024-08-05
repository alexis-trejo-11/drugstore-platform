package at.backend.drugstore.microservice.common_models.ExternalService.Client;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ExternalClientService {
    CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    CompletableFuture<Result<ClientDTO>> findClientById(Long clientId);
}
