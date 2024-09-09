package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    Order createOrder(OrderInsertDTO orderInsertDTO);
    OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO);
    Result<Void> processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO);
    Optional<OrderDTO> getOrderById(Long orderId);
    String deliveryOrder(Long orderId, boolean isOrderDelivered);
    Result<Void> validateOrderForDelivery(OrderDTO orderDTO);
    CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest);
    void addPaymentIdByOrderId(Long orderId, Long paymentId);
    void processOrderNotPaid(Long orderId);
}