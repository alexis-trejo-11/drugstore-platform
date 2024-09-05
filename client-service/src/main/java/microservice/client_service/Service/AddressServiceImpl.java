package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.persistence.EntityNotFoundException;
import microservice.client_service.Mappers.AddressMapper;
import microservice.client_service.Model.Address;
import microservice.client_service.Model.Client;
import microservice.client_service.Repository.AddressRepository;
import microservice.client_service.Repository.ClientRepository;
import microservice.client_service.Mappers.dtoMapper;
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
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              ClientRepository clientRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public Result<Void> addAddress(AddressInsertDTO addressInsertDTO, Long clientId) {
            Client client = clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + clientId));

             List<Address> addresses = client.getAddresses();
            if (addresses.size() > 5) {
                return Result.error("Limit Of Addresses Reached, Remove Any Address To Succeed");
            }

            Address address = addressMapper.insertDtoToEntity(addressInsertDTO);
            address.setClient(client);

            addressRepository.saveAndFlush(address);

            return Result.success();
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "addresses", key = "#addressId", unless = "#result == null || #result.isEmpty()")
    public CompletableFuture<Optional<AddressDTO>> getAddressById(Long addressId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            return optionalAddress.map(addressMapper::entityToDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "clientAddresses", key = "#clientId")
    public CompletableFuture<List<AddressDTO>> getAddressesByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Client> client = clientRepository.findById(clientId);

            List<Address> addresses = addressRepository.findByClientId(clientId);
            return addresses.stream()
                    .map(addressMapper::entityToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public void updateAddressFromClientList(AddressUpdateDTO addressUpdateDTO, Long clientID) {
         Client client = clientRepository.findById(clientID).orElse(null);
         if (client == null) { return; }

         List<Address> addresses = client.getAddresses();
         Address address = addresses.get(addressUpdateDTO.getAddressIndex());

        Address addressUpdated = dtoMapper.insertDtoToEntity(address, addressUpdateDTO);
        addressRepository.saveAndFlush(addressUpdated);
    }

    @Override
    @Transactional
    public void deleteAddressFromClientList(int index, Long clientID) {
        Client client = clientRepository.findById(clientID).orElse(null);
        if (client == null) { return; }

        List<Address> addresses = client.getAddresses();
        Address address = addresses.get(index);

        addressRepository.delete(address);
    }


    @Override
    @Transactional
    public void deleteAddressById(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public boolean validateExistingAddress(Long addressId) {
        return addressRepository.findById(addressId).isPresent();
    }
}