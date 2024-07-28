package microservice.ecommerce_order_service.Mapper;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemDTO;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);
    @Mappings({
            @Mapping(target = "productId", source = "cartItemDTO.productId"),
            @Mapping(target = "productQuantity", source = "cartItemDTO.productQuantity"),
            @Mapping(target = "order", source = "order"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
    })
    OrderItem cartItemDTOToOrderItem(CartItemDTO cartItemDTO, Order order);


    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "itemTotal", ignore = true)
    OrderItemDTO entityToDTO(OrderItem orderItem);
}
