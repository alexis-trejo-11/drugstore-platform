package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ExternalServiceFacade {
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;

    public ExternalServiceFacade(ExternalClientService externalClientService, ExternalAddressService externalAddressService) {
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
    }

    public CompletableFuture<Result<ClientDTO>> getClientById(Long clientId) {
        return externalClientService.findClientById(clientId);
    }

    public CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId) {
        return externalAddressService.getAddressId(addressId);
    }
}