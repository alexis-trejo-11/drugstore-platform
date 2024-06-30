package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.client_service.Model.Address;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.AddressRepository;
import microservice.client_service.Repository.ClientRepository;
import microservice.client_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.logging.Logger;


@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private static final Logger logger = Logger.getLogger(AddressService.class.getName());

    @Autowired
    public AddressService(AddressRepository addressRepository, ClientRepository clientRepository) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
    }

    @Async
    @Transactional
        public CompletableFuture<Result<Void>> addAddress(AddressInsertDTO addressInsertDTO, Long clientId) {
        try {
                Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
               return CompletableFuture.completedFuture(Result.error("Client With Id: " + clientId + "Not Found."));
            } else {
                Address address = ModelTransformer.insertDtoToAddress(addressInsertDTO);
                address.setClient(client.get());

                addressRepository.saveAndFlush(address);

                return CompletableFuture.completedFuture(Result.success());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(new Throwable("An error occurred while adding address: " + e.getMessage()));
        }
    }

    @Async
    public CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId) {
        try {
            Optional<Address> address = addressRepository.findById(addressId);
            if (address.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Address with Id: " + addressId + " Not Found"));
            } else {
                AddressDTO addressDTO = ModelTransformer.addressToReturnDTO(address.get());
                return CompletableFuture.completedFuture(Result.success(addressDTO));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Finding Address", e));
        }
    }

    @Async
    public CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId) {
        try {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Client with Id: " + clientId + " Not Found"));
            } else {
                List<Address> addresses = addressRepository.findByClientId(clientId);

               List<AddressDTO> addressDTOS  = addresses.stream()
                        .map(ModelTransformer::addressToReturnDTO)
                        .collect(Collectors.toList());

               return CompletableFuture.completedFuture(Result.success(addressDTOS));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Finding Addresses", e));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> updateAddressById(AddressInsertDTO addressInsertDTO, Long addressId) {
        try {
            Optional<Address> addressFounded = addressRepository.findById(addressId);
            if (addressFounded.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Address with Id:" + addressId + " Not Found"));
            } else {
                Address addressUpdated = ModelTransformer.insertDtoUpdate(addressFounded.get(), addressInsertDTO);

                addressRepository.saveAndFlush(addressUpdated);

                return CompletableFuture.completedFuture(Result.success());
            }
        } catch (Exception e) {
            logger.severe("This is a severe error message");
            return CompletableFuture.failedFuture(new Throwable("An error occurred while updating the address", e));

        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<String>> deleteAddressById(Long addressId) {
        try {
            Optional<Address> addressFounded = addressRepository.findById(addressId);
            if (addressFounded.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Address with Id: " + addressId + " Not Found"));
            } else {
                addressRepository.deleteById(addressId);

                return CompletableFuture.completedFuture(Result.success("Address Successfully Deleted"));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An error occurred while deleting the address", e));

        }

    }

}





