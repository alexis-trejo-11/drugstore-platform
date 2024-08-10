package microservice.test_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartItemDTO;
import microservice.test_service.Model.Afterward;
import microservice.test_service.Model.CartItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AfterwardMapper {

    Afterward cartItemToEntity(CartItem cartItem, Long clientId);
    CartItemDTO entityToCartItemDTO(Afterward afterward);
    CartItem entityToCartItem(Afterward afterward);
}
