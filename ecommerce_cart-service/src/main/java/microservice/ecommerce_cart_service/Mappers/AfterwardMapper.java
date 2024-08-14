package microservice.ecommerce_cart_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AfterwardMapper {

    Afterward cartItemToEntity(CartItem cartItem, Long clientId);
    CartItemDTO entityToCartItemDTO(Afterward afterward);
    CartItem entityToCartItem(Afterward afterward);
}
