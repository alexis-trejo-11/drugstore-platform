package microservice.client_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import microservice.client_service.Model.Address;
import microservice.client_service.Model.Client;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ModelTransformer {

    public static Client insertDtoToClient(ClientInsertDTO clientInsertDTO) {
        Client client = new Client();
        client.setFirstName(clientInsertDTO.getFirstName());
        client.setLastName(clientInsertDTO.getLastName());
        client.setPhone(clientInsertDTO.getPhone());
        client.setBirthdate(clientInsertDTO.getBirthdate());
        client.setClientPremium(false);
        client.setLoyaltyPoints(0);
        client.setJoinedAt(LocalDateTime.now());
        client.setActive(true);
        client.setLastAction(LocalDateTime.now());

        return client;
    }

    public static ClientDTO clientToReturnDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setFirstName(client.getFirstName());
        clientDTO.setLastName(client.getLastName());
        clientDTO.setBirthdate(client.getBirthdate());
        clientDTO.setPhone(client.getPhone());
        clientDTO.setLoyaltyPoints(client.getLoyaltyPoints());

        return clientDTO;
    }

    public static Address insertDtoToAddress(AddressInsertDTO addressInsertDTO) {
        Address address = new Address();
        address.setStreet(addressInsertDTO.getStreet());
        address.setHouseNumber(addressInsertDTO.getHouseNumber());
        address.setCity(addressInsertDTO.getCity());
        address.setZipCode(Integer.parseInt(addressInsertDTO.getZipCode()));
        address.setDescription(addressInsertDTO.getDescription());

        if (addressInsertDTO.getAddressType() != null) {
            address.setAddressType(Address.AddressType.valueOf(addressInsertDTO.getAddressType()));
        } else {
            address.setAddressType(Address.AddressType.HOUSE);
        }

        if (addressInsertDTO.getInnerNumber() != null) {
            address.setInnerNumber(addressInsertDTO.getInnerNumber());
        }

        address.setCountry(addressInsertDTO.getCountry());
        address.setState(addressInsertDTO.getState());
        address.setNeighborhood(addressInsertDTO.getNeighborhood());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());


        return address;
    }

    public static Address insertDtoUpdate(Address address, AddressInsertDTO addressInsertDTO) {
        Field[] fields = AddressInsertDTO.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(addressInsertDTO);
                if (value != null) {
                    Field addressField = Address.class.getDeclaredField(field.getName());
                    addressField.setAccessible(true);
                    addressField.set(address, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.fillInStackTrace();
                throw new RuntimeException("Can't Parse Data");
            }
        }
        return address;
    }

    public static AddressDTO addressToReturnDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setNeighborhood(address.getNeighborhood());
        addressDTO.setHouseNumber(address.getHouseNumber());
        addressDTO.setCity(address.getCity());
        addressDTO.setZipCode(address.getZipCode());
        addressDTO.setState(address.getState());
        addressDTO.setDescription(address.getDescription());
        addressDTO.setAddressType(String.valueOf(address.getAddressType()));
        addressDTO.setInnerNumber(address.getInnerNumber());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setClientId(address.getClient().getId());


        return addressDTO;
    }


}
