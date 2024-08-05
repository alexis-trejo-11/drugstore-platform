package at.backend.drugstore.microservice.common_models.ExternalService.Order;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public interface ExternalOrderService {
    CompletableFuture<Long> createOrderAndGetId(OrderInsertDTO orderInsertDTO);
    CompletableFuture<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId);
    CompletableFuture<Void> addPaymentIdByOrderId(Long paymentId, Long orderId);
    CompletableFuture<OrderDTO> getOrderById(Long orderId);
}
