package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<Order> createOrderAsync(OrderInsertDTO orderInsertDTO);
    OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO);
    CompletableFuture<Result<Void>> processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO);
    CompletableFuture<Optional<OrderDTO>> getOrderById(Long orderId);
    CompletableFuture<String> deliveryOrder(Long orderId, boolean isOrderDelivered);
    CompletableFuture<Result<Void>> validateOrderForDelivery(OrderDTO orderDTO);
    CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest);
    void addPaymentIdByOrderId(Long orderId, Long paymentId);
    CompletableFuture<Void> processOrderNotPaid(Long orderId);
}