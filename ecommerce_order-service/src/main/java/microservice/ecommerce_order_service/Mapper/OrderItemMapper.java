package microservice.ecommerce_order_service.Mapper;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemDTO;
import microservice.ecommerce_order_service.Model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mappings({
            @Mapping(target = "orderId", source = "order.id"),
            @Mapping(target = "productId", source = "productId"),
            @Mapping(target = "quantity", source = "quantity")
    })
    OrderItemDTO entityToDTO(OrderItem orderItem);
}
