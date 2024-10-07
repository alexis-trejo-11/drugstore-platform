package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Address;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AddressFacadeService {
    CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId);
    CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId);
}
