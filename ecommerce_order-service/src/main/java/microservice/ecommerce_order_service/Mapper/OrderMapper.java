    package microservice.ecommerce_order_service.Mapper;

    import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
    import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
    import microservice.ecommerce_order_service.Model.Order;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.Mappings;


    @Mapper(componentModel = "spring")
    public interface OrderMapper {

        @Mappings({
                @Mapping(target = "status", expression = "java(at.backend.drugstore.microservice.common_models.DTO.Order.OrderStatus.PENDING)"),
                @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())"),
                @Mapping(target = "lastOrderUpdate", expression = "java(java.time.LocalDateTime.now())"),
                @Mapping(target = "deliveryTries", constant = "0"),
                @Mapping(target = "clientId", source = "addressDTO.clientId")
        })
        Order insertDtoToEntity(OrderInsertDTO orderInsertDTO);


        @Mapping(target = "items", ignore = true)
        OrderDTO entityToDTO(Order order);



    }
