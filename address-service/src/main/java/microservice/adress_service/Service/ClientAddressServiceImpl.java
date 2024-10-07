package microservice.adress_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.persistence.EntityNotFoundException;
import microservice.adress_service.Mappers.AddressMapper;
import microservice.adress_service.Model.ClientAddress;
import microservice.adress_service.Repository.AddressRepository;
import microservice.adress_service.Mappers.dtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ClientAddressServiceImpl implements ClientAddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final ClientFacadeService clientFacadeService;

    @Autowired
    public ClientAddressServiceImpl(AddressRepository addressRepository,
                                    AddressMapper addressMapper,
                                    ClientFacadeService clientFacadeService) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.clientFacadeService = clientFacadeService;
    }

    @Override
    @Transactional
    public Result<Void> addAddress(AddressInsertDTO addressInsertDTO, Long clientId) {
        boolean isClientValidated = clientFacadeService.validateExistingClient(clientId);
        if (!isClientValidated) {
            throw new EntityNotFoundException("Client not found");
        }

            List<ClientAddress> addresses = addressRepository.findByClientId(clientId);
            if (addresses.size() >= 5) {
                return Result.error("Addresses limit reached, delete one address to succeed");
            }

            ClientAddress address = addressMapper.insertDtoToEntity(addressInsertDTO);
            address.setClientId(clientId);

            addressRepository.saveAndFlush(address);
            return Result.success();
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "addresses", key = "#addressId", unless = "#result == null || #result.isEmpty()")
    public CompletableFuture<Optional<AddressDTO>> getAddressById(Long addressId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<ClientAddress> optionalAddress = addressRepository.findById(addressId);
            return optionalAddress.map(addressMapper::entityToDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<AddressDTO>> getAddressesByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            boolean isClientValidated = clientFacadeService.validateExistingClient(clientId);
            if (!isClientValidated) {
                throw new EntityNotFoundException("Client not found");
            }

            List<ClientAddress> addresses = addressRepository.findByClientId(clientId);
            return addresses.stream()
                    .map(addressMapper::entityToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public void updateAddressFromClient(AddressUpdateDTO addressUpdateDTO, Long clientId) {
        boolean isClientValidated = clientFacadeService.validateExistingClient(clientId);
        if (!isClientValidated) {
            throw new EntityNotFoundException("Client not found");
        }

        int rowsUpdated = addressRepository.updateClientAddress(addressUpdateDTO.getAddressId(), clientId, addressUpdateDTO);
        if (rowsUpdated == 0) {
            throw new EntityNotFoundException("Address not found for clientId: " + clientId);
        }
    }

    @Override
    @Transactional
    public void deleteAddressFromClient(Long addressId, Long clientId) {
        Optional<ClientAddress> optionalClientAddress = addressRepository.findByIdAndClientId(addressId,clientId);
        if (optionalClientAddress.isEmpty()) {
            throw new EntityNotFoundException("Address not found");
        }

        addressRepository.delete(optionalClientAddress.get());
    }
}