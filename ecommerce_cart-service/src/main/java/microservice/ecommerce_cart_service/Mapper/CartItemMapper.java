package microservice.ecommerce_cart_service.Mapper;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemDTO entityToDTO(CartItem cartItem);

    @Mappings({
            @Mapping(target = "productId", source = "productDTO.id"),
            @Mapping(target = "productName", source = "productDTO.name"),
            @Mapping(target = "productPrice", source = "productDTO.price"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "itemTotal", expression = "java(microservice.ecommerce_cart_service.Mapper.CartItemMapperUtil.calculateItemTotal(productDTO, quantity))"),
            @Mapping(target = "cart", source = "cart"),
            @Mapping(target = "id", ignore = true)
    })
    CartItem productDtoToCartItem(ProductDTO productDTO, int quantity, Cart cart);

}
