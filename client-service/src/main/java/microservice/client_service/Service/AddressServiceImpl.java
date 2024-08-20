package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.client_service.Mappers.AddressMapper;
import microservice.client_service.Model.Address;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.AddressRepository;
import microservice.client_service.Repository.ClientRepository;
import microservice.client_service.Mappers.dtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, ClientRepository clientRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> addAddress(AddressInsertDTO addressInsertDTO, Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return Result.error("Client With Id: " + clientId + " Not Found.");
            }

            Address address = addressMapper.insertDtoToEntity(addressInsertDTO);
            address.setClient(client.get());

            addressRepository.saveAndFlush(address);

            return Result.success();
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Optional<AddressDTO>> getAddressById(Long addressId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            return optionalAddress.map(addressMapper::entityToDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Client> client = clientRepository.findById(clientId);
            if (client.isEmpty()) {
                return Result.error("Client with Id: " + clientId + " Not Found");
            }

            List<Address> addresses = addressRepository.findByClientId(clientId);

            List<AddressDTO> addressDTOS = addresses.stream()
                    .map(addressMapper::entityToDTO)
                    .collect(Collectors.toList());

            return Result.success(addressDTOS);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> updateAddressById(AddressInsertDTO addressInsertDTO, Long addressId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            if (optionalAddress.isEmpty()) {
                return Result.error("Address with Id:" + addressId + " Not Found");
            }
            Address addressUpdated = dtoMapper.insertDtoToEntity(optionalAddress.get(), addressInsertDTO);

            addressRepository.saveAndFlush(addressUpdated);

            return Result.success();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<String>> deleteAddressById(Long addressId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Address> addressFounded = addressRepository.findById(addressId);
            if (addressFounded.isEmpty()) {
                return Result.error("Address with Id: " + addressId + " Not Found");
            }
            addressRepository.deleteById(addressId);

            return Result.success("Address Successfully Deleted");
        });
    }
}