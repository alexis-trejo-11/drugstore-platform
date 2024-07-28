package microservice.ecommerce_cart_service.Service.Extensions;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import microservice.ecommerce_cart_service.Service.Factory.OrderDTOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderCreator {
    private final ExternalOrderService externalOrderService;

    @Autowired
    public OrderCreator(ExternalOrderService externalOrderService) {
        this.externalOrderService = externalOrderService;
    }

    public Long createOrder(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = OrderDTOFactory.createOrderInsertDTO(cartDTO, clientDTO, addressId);
        return externalOrderService.createOrderAndGetId(orderInsertDTO);
    }
}
