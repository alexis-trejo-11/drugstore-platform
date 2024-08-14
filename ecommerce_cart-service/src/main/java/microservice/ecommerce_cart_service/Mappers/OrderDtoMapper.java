package microservice.ecommerce_cart_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoMapper {
    public OrderInsertDTO createOrderInsertDTO(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = new OrderInsertDTO();
        orderInsertDTO.setClientId(clientDTO.getId());
        orderInsertDTO.setCartDTO(cartDTO);
        orderInsertDTO.setAddressId(addressId);
        return orderInsertDTO;
    }
}
