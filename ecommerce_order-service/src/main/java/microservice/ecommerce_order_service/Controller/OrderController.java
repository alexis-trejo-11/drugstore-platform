package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/orders")
@RateLimiter(name = "orderApiLimiter")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/complete-order")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> completeOrder(@Valid @RequestBody CompleteOrderRequest completeOrderRequest) {
        log.info("Received request to complete order: {}", completeOrderRequest);

        if (!completeOrderRequest.isOrderPaid()) {
            return orderService.processOrderNotPaid(completeOrderRequest.getOrderId())
                    .thenApply(voidResult -> {
                        log.info("Order payment processed as failed for order: {}", completeOrderRequest.getOrderId());
                        return ResponseEntity.ok(new ApiResponse<>(true, null, "Order marked as not paid", 200));
                    });
        }

        // Fetch client data needed to complete the order
        return orderService.bringClientDataToCompleteOrder(completeOrderRequest)
                .thenCompose(clientData -> {
                    log.debug("Client data fetched successfully: {}", clientData);

                    // Process order payment with the fetched client data
                    return orderService.processOrderPayment(completeOrderRequest, clientData.getAddressDTO(), clientData.getClientDTO(), clientData.getOrderDTO()
                    ).thenApply(processResult -> {
                        // Check if the payment process was successful
                        if (!processResult.isSuccess()) {
                            log.warn("Order payment processing failed: {}", processResult.getErrorMessage());
                            // Return a bad request response with the error message
                            return ResponseEntity.badRequest()
                                    .body(new ApiResponse<>(false, null, processResult.getErrorMessage(), 400));
                        }

                        log.info("Order payment processed successfully for order: {}", completeOrderRequest.getOrderId());
                        // Return a success response indicating the order is completed
                        return ResponseEntity.ok()
                                .body(new ApiResponse<>(true, null, "Order completed", 200));
                    });
                });

    }


    @PutMapping("/{orderId}/payment/{paymentId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> addPaymentIdByOrderId(@PathVariable Long orderId, @PathVariable Long paymentId) {
        return CompletableFuture.runAsync(() -> orderService.addPaymentIdByOrderId(orderId, paymentId))
                .thenApply(v -> ResponseEntity.ok()
                        .body(new ApiResponse<>(true, null, "Order Updated", 200))
                );

    }

    @GetMapping("/{orderId}")
    public CompletableFuture<ResponseEntity<ApiResponse<OrderDTO>>> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .thenApply(orderOpt -> orderOpt
                        .map(orderDTO -> ResponseEntity.ok()
                                .body(new ApiResponse<>(true, orderDTO, "Order successfully fetched.", 200)))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found", 404)))
                );

    }

    @PutMapping("/deliver/{orderId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deliverOrder(@PathVariable Long orderId, @RequestParam boolean isDelivered) {
        return orderService.getOrderById(orderId)
                .thenCompose(orderOpt -> {
                    if (orderOpt.isEmpty()) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found.", 404)));
                    }

                    return orderService.validateOrderForDelivery(orderOpt.get())
                            .thenCompose(validateResult -> {
                                if (!validateResult.isSuccess()) {
                                    return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                                            .body(new ApiResponse<>(false, null, validateResult.getErrorMessage(), 400)));
                                }

                                return orderService.deliveryOrder(orderId, isDelivered)
                                        .thenApply(deliveryStatus -> ResponseEntity.ok()
                                                .body(new ApiResponse<Void>(true, null, deliveryStatus, 200)));
                            });
                });
    }
}