package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client;

import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ClientFacadeService {
    CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    CompletableFuture<Result<ClientDTO>> findClientById(Long clientId);
}
