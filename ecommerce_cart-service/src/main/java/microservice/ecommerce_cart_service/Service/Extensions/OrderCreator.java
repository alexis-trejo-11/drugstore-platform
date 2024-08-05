package microservice.ecommerce_cart_service.Service.Extensions;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import microservice.ecommerce_cart_service.Service.Factory.OrderDTOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderCreator {
    private final ExternalOrderService externalOrderService;

    @Autowired
    public OrderCreator(ExternalOrderService externalOrderService) {
        this.externalOrderService = externalOrderService;
    }

    public CompletableFuture<Long> createOrder(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = OrderDTOFactory.createOrderInsertDTO(cartDTO, clientDTO, addressId);

        // Call the asynchronous method and handle the result
        return externalOrderService.createOrderAndGetId(orderInsertDTO)
                .thenApply(orderId -> {
                    // Optionally handle the result here
                    return orderId;
                });
    }
}
