package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderPaymentStatus;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_order_service.Model.CompleteOrderData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    OrderDTO createOrder(OrderInsertDTO orderInsertDTO);
    Result<Void> processOrderPaid(CompleteOrderData completeOrderData);
    Optional<OrderDTO> getOrderById(Long orderId);
    String deliveryOrder(Long orderId, boolean isOrderDelivered);
    Result<Void> validateOrderForDelivery(OrderDTO orderDTO);
    CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(OrderPaymentStatus orderPaymentStatus);
    void addPaymentIdByOrderId(Long orderId, Long paymentId);
    void processOrderNotPaid(Long orderId);
}