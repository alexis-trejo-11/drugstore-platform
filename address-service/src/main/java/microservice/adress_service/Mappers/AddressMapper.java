package microservice.adress_service.Mappers;


import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.Models.Address.AddressType;
import microservice.adress_service.Model.ClientAddress;
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
            @Mapping(target = "clientId", ignore = true)
    })
    ClientAddress insertDtoToEntity(AddressInsertDTO addressInsertDTO);

    @Mappings({
            @Mapping(target = "addressType", expression = "java(clientAddress.getAddressType().toString())"),
    })
    AddressDTO entityToDTO(ClientAddress clientAddress);


    @Named("mapAddressType")
    default AddressType mapAddressType(AddressInsertDTO addressInsertDTO) {
        if (addressInsertDTO.getAddressType() != null) {
            return AddressType.valueOf(addressInsertDTO.getAddressType());
        } else {
            return AddressType.HOUSE;
        }
    }

}