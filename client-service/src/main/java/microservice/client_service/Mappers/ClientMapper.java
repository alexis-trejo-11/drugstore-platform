package microservice.client_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import microservice.client_service.Model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "joinedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastAction", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "clientPremium", constant = "true")
    @Mapping(target = "loyaltyPoints", constant = "0")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Client insertDtoToEntity(ClientInsertDTO clientInsertDTO);


    ClientDTO entityToDTO(Client client);
}
