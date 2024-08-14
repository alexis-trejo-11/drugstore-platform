package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AddressService {
    CompletableFuture<Result<Void>> addAddress(AddressInsertDTO addressInsertDTO, Long clientId);
    CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId);
    CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId);
    CompletableFuture<Result<Void>> updateAddressById(AddressInsertDTO addressInsertDTO, Long addressId);
    CompletableFuture<Result<String>> deleteAddressById(Long addressId);
}
