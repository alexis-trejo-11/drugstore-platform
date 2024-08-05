package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/client-orders")
@RateLimiter(name = "orderApiLimiter")
public class ClientOrderController {
    private final OrderService orderService;

    @Autowired
    public ClientOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ApiResponse<OrderDTO>>> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO) {
        log.info("Received create order request: {}", orderInsertDTO);
        return orderService.createOrderAsync(orderInsertDTO)
                .thenApply(order -> {
                    log.info("Order created successfully with ID: {}", order.getId());
                    OrderDTO orderDTO = orderService.processOrderCreation(order, orderInsertDTO.getClientId(), orderInsertDTO.getCartDTO());

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ApiResponse<>(true, orderDTO, "Order created successfully.", 201));
                });
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @GetMapping("/current/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Page<OrderDTO>>>> getCurrentOrdersByClientId(@PathVariable Long clientId,
                                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                                     @RequestParam(defaultValue = "10") int size) {
        return orderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return orderService.getCurrentOrdersByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ApiResponse<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @GetMapping("/completed/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Page<OrderDTO>>>> getCompletedOrdersByClientId(@PathVariable Long clientId,
                                                                                              @RequestParam(defaultValue = "0") int page,
                                                                                              @RequestParam(defaultValue = "10") int size) {
        return orderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return orderService.getCompletedOrdersByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ApiResponse<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }

    @GetMapping("/cancel/{orderId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> cancelOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .thenCompose(optionalOrderDTO -> {
                    if (optionalOrderDTO.isEmpty()) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found.", 400)));
                    }

                    return orderService.cancelOrder(orderId)
                            .thenApply(orderResult -> {
                                if (!orderResult.isSuccess()) {
                                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                            .body(new ApiResponse<>(false, null, orderResult.getErrorMessage(), 400));
                                }

                                return ResponseEntity.status(HttpStatus.OK)
                                        .body(new ApiResponse<>(true, null, "Order successfully canceled.", 200));
                            });
                });
    }
}