package microservice.ecommerce_cart_service.Service.Factory;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;

public class OrderDTOFactory {
    public static OrderInsertDTO createOrderInsertDTO(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = new OrderInsertDTO();
        orderInsertDTO.setClientId(clientDTO.getId());
        orderInsertDTO.setCartDTO(cartDTO);
        orderInsertDTO.setAddressId(addressId);
        return orderInsertDTO;
    }
}
