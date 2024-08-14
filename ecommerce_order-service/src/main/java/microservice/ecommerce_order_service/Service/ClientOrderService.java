package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface ClientOrderService {
    CompletableFuture<Page<OrderDTO>> getCurrentOrdersByClientId(Long clientId, Pageable pageable);
    CompletableFuture<Page<OrderDTO>> getCompletedOrdersByClientId(Long clientId, Pageable pageable);
    CompletableFuture<Page<OrderDTO>> getCancelledOrdersByClientId(Long clientId, Pageable pageable);
    CompletableFuture<Page<OrderDTO>> getOrdersToBeValidatedByClientId(Long clientId, Pageable pageable);
    CompletableFuture<Result<Void>> cancelOrder(Long orderId);
    CompletableFuture<Boolean> validateExistingClient(Long clientId);

}