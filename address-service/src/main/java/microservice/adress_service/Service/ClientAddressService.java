package microservice.adress_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ClientAddressService {
    Result<Void> addAddress(AddressInsertDTO addressInsertDTO, Long clientId);
    CompletableFuture<Optional<AddressDTO>> getAddressById(Long addressId);
    CompletableFuture<List<AddressDTO>> getAddressesByClientId(Long clientId);
    void updateAddressFromClient(AddressUpdateDTO addressUpdateDTO, Long clientId);
    void deleteAddressFromClient(Long addressId, Long clientId);
}
