package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<Order> createOrderAsync(OrderInsertDTO orderInsertDTO);
    OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO);
    CompletableFuture<Result<Void>> processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO);
    CompletableFuture<Optional<OrderDTO>> getOrderById(Long orderId);
    CompletableFuture<Boolean> validateExistingClient(Long clientId);
    CompletableFuture<Page<OrderDTO>> getCurrentOrdersByClientId(Long clientId, Pageable pageable);
    CompletableFuture<Page<OrderDTO>> getCompletedOrdersByClientId(Long clientId, Pageable pageable);
    CompletableFuture<String> deliveryOrder(Long orderId, boolean isOrderDelivered);
    CompletableFuture<Result<Void>> validateOrderForDelivery(OrderDTO orderDTO);
    CompletableFuture<Result<Void>> cancelOrder(Long orderId);
    CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest);
    void addPaymentIdByOrderId(Long orderId, Long paymentId);
    CompletableFuture<Void> processOrderNotPaid(Long orderId);
}