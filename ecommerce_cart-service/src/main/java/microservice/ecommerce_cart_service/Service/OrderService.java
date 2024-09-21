package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.concurrent.CompletableFuture;

public interface OrderService {
    PaymentDTO initPayment(ClientEcommerceDataDTO clientData, Long cardId, Long orderId);
    CompletableFuture<Result<ClientEcommerceDataDTO>> aggregateClientData(Long clientId);
    CompletableFuture<Long> createOrder(CartDTO cartDTO, ClientDTO clientDTO, Long addressId);
}
