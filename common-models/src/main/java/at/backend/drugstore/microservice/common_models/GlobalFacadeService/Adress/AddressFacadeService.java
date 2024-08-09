package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Adress;

import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AddressFacadeService {
    CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId);
    CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId);
}
