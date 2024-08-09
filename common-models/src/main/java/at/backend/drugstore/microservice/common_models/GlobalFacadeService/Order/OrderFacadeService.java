package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order;

import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderInsertDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface OrderFacadeService {
    CompletableFuture<Long> createOrderAndGetId(OrderInsertDTO orderInsertDTO);
    CompletableFuture<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId);
    CompletableFuture<Void> addPaymentIdByOrderId(Long paymentId, Long orderId);
    CompletableFuture<OrderDTO> getOrderById(Long orderId);
}
