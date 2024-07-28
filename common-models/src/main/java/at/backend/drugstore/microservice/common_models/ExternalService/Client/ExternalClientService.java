package at.backend.drugstore.microservice.common_models.ExternalService.Client;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

@Service
public interface ExternalClientService {
    Result<ClientDTO> createClient(ClientInsertDTO clientInsertDTO);
    Result<ClientDTO> findClientById(Long clientId);
}
