package microservice.client_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.client_service.Mappers.AddressMapper;
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
import java.util.stream.Collectors;


@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, ClientRepository clientRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
        this.addressMapper = addressMapper;
    }

    @Async
    @Transactional
        public Result<Void> addAddress(AddressInsertDTO addressInsertDTO, Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            return Result.error("Client With Id: " + clientId + "Not Found.");
        }

        Address address = addressMapper.insertDtoToEntity(addressInsertDTO);
        address.setClient(client.get());

        addressRepository.saveAndFlush(address);

        return Result.success();
    }

    @Async
    public Result<AddressDTO> getAddressById(Long addressId) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            return Result.error("Address with Id: " + addressId + " Not Found");
        }
        AddressDTO addressDTO = addressMapper.entityToDTO(address.get());
        return Result.success(addressDTO);
    }

    @Async
    public Result<List<AddressDTO>> getAddressesByClientId(Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            return Result.error("Client with Id: " + clientId + " Not Found");
        }

        List<Address> addresses = addressRepository.findByClientId(clientId);

        List<AddressDTO> addressDTOS  = addresses.stream()
                .map(addressMapper::entityToDTO)
                .collect(Collectors.toList());

        return Result.success(addressDTOS);
    }

    @Async
    @Transactional
    public Result<Void> updateAddressById(AddressInsertDTO addressInsertDTO, Long addressId) {
        Optional<Address> addressFounded = addressRepository.findById(addressId);
        if (addressFounded.isEmpty()) {
            return Result.error("Address with Id:" + addressId + " Not Found");
        }
        Address addressUpdated = ModelTransformer.insertDtoUpdate(addressFounded.get(), addressInsertDTO);

        addressRepository.saveAndFlush(addressUpdated);

        return Result.success();
    }

    @Async
    @Transactional
    public Result<String> deleteAddressById(Long addressId) {
        Optional<Address> addressFounded = addressRepository.findById(addressId);
        if (addressFounded.isEmpty()) {
            return Result.error("Address with Id: " + addressId + " Not Found");
        }
        addressRepository.deleteById(addressId);

        return Result.success("Address Successfully Deleted");
    }

}





