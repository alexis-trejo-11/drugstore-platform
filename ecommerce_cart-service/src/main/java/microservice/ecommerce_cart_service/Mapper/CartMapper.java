package microservice.ecommerce_cart_service.Mapper;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "productsIds", ignore = true)
    CartDTO entityToDTO(Cart cart);
}
