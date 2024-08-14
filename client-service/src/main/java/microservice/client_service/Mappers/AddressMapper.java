package microservice.client_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import microservice.client_service.Model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mappings({
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "zipCode", expression = "java(Integer.parseInt(addressInsertDTO.getZipCode()))"),
            @Mapping(target = "addressType", source = "addressInsertDTO", qualifiedByName = "mapAddressType"),
            @Mapping(target = "innerNumber", source = "addressInsertDTO.innerNumber"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "client", ignore = true)
    })
    Address insertDtoToEntity(AddressInsertDTO addressInsertDTO);

    @Mappings({
            @Mapping(target = "addressType", expression = "java(address.getAddressType().toString())"),
            @Mapping(target = "clientId", source = "address", qualifiedByName = "extractClientId")
    })
    AddressDTO entityToDTO(Address address);


    @Named("mapAddressType")
    default Address.AddressType mapAddressType(AddressInsertDTO addressInsertDTO) {
        if (addressInsertDTO.getAddressType() != null) {
            return Address.AddressType.valueOf(addressInsertDTO.getAddressType());
        } else {
            return Address.AddressType.HOUSE;
        }
    }

    @Named("extractClientId")
    default Long extractClientId(Address address) {
        if (address.getClient() != null) {
            return address.getClient().getId();
        } else {
            return null;
        }
    }
}