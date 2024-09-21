package microservice.ecommerce_cart_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface AfterwardMapper {

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    Afterward cartItemToEntity(CartItem cartItem, Long clientId);

    CartItemDTO entityToCartItemDTO(Afterward afterward);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    CartItem entityToCartItem(Afterward afterward);
}
