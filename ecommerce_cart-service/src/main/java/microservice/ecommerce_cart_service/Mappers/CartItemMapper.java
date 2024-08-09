package microservice.ecommerce_cart_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mappings({
            @Mapping(target = "productQuantity", source = "cartItem.quantity"),
            @Mapping(target = "productUnitPrice", source = "cartItem.productPrice"),
    })
    CartItemDTO entityToDTO(CartItem cartItem);

    @Mappings({
            @Mapping(target = "productId", source = "productDTO.id"),
            @Mapping(target = "productName", source = "productDTO.name"),
            @Mapping(target = "productPrice", source = "productDTO.price"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "itemTotal", ignore = true),
            @Mapping(target = "cart", source = "cart"),
            @Mapping(target = "id", ignore = true)
    })
    CartItem productDtoToCartItem(ProductDTO productDTO, int quantity, Cart cart);

    @Mappings({
            @Mapping(target = "productId", source = "cartItemDTO.productId"),
            @Mapping(target = "productName", source = "cartItemDTO.productName"),
            @Mapping(target = "productQuantity", source = "cartItemDTO.productQuantity"),
            @Mapping(target = "productUnitPrice", source = "cartItemDTO.productUnitPrice"),
            @Mapping(target = "orderId", ignore = true),
            @Mapping(target = "itemTotal", ignore = true),

    })
    OrderItemDTO cartItemToOrderDTO(CartItemDTO cartItemDTO);
}
