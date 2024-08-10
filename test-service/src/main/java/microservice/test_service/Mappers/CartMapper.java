package microservice.test_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import microservice.test_service.Model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "clientId", source = "cart.clientId")
    @Mapping(target = "productsIds", ignore = true)
    CartDTO entityToDTO(Cart cart);


}
