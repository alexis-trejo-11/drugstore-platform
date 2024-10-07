    package microservice.ecommerce_order_service.Mapper;

    import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
    import microservice.ecommerce_order_service.Model.Order;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.Mappings;


    @Mapper(componentModel = "spring")
    public interface OrderMapper {

        @Mappings({
                @Mapping(target = "status", expression = "java(at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus.PENDING_PAYMENT)"),
                @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
                @Mapping(target = "lastOrderUpdate", expression = "java(java.time.LocalDateTime.now())"),
                @Mapping(target = "deliveryTries", constant = "0"),
                @Mapping(target = "clientId", source = "clientId"),
                @Mapping(target = "items", ignore = true),
                @Mapping(target = "shippingData", ignore = true),
                @Mapping(target = "id", ignore = true),
                @Mapping(target = "paymentId", ignore = true),
                @Mapping(target = "addressId", source = "addressId")
        })
        Order insertDtoToEntity(Long addressId, Long clientId);


        @Mapping(target = "items", ignore = true)
        @Mapping(target = "status", source = "status")
        OrderDTO entityToDTO(Order order);



    }
