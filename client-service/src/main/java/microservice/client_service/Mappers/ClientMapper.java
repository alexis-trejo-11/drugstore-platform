package microservice.client_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientUpdateDTO;
import microservice.client_service.Model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    void updateClientFromDto(ClientInsertDTO clientInsertDTO, @MappingTarget Client client);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastAction", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "clientPremium", constant = "true")
    @Mapping(target = "loyaltyPoints", constant = "0")
    @Mapping(target = "id", ignore = true)
    Client insertDtoToEntity(ClientInsertDTO clientInsertDTO);


    ClientDTO entityToDTO(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastAction", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "clientPremium", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    void updateClientFromDto(ClientUpdateDTO clientUpdateDTO, @MappingTarget Client client);

}
