package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AddressService {
    Result<Void> addAddress(AddressInsertDTO addressInsertDTO, Long clientId);
    CompletableFuture<Optional<AddressDTO>> getAddressById(Long addressId);
    CompletableFuture<List<AddressDTO>> getAddressesByClientId(Long clientId);
    void updateAddressFromClientList(AddressUpdateDTO addressUpdateDTO, Long clientID);
    void deleteAddressById(Long addressId);
    void deleteAddressFromClientList(int index, Long clientID);
    boolean validateExistingAddress(Long addressId);
}
