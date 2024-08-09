package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @Operation(summary = "Create a new order", description = "Creates a new order based on the provided order details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid order details.")
    })
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<OrderDTO>>> createOrder(
            @Valid @RequestBody @Parameter(description = "Details of the order to be created") OrderInsertDTO orderInsertDTO) {
        log.info("Received create order request: {}", orderInsertDTO);
        return orderService.createOrderAsync(orderInsertDTO)
                .thenApply(order -> {
                    log.info("Order created successfully with ID: {}", order.getId());
                    OrderDTO orderDTO = orderService.processOrderCreation(order, orderInsertDTO.getClientId(), orderInsertDTO.getCartDTO());

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseWrapper<>(true, orderDTO, "Order created successfully.", 201));
                });
    }



    @Operation(summary = "Complete an order", description = "Completes an order by processing payment and updating the order status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as completed successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to process payment or invalid order details.")
    })
    @PutMapping("/complete-order")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> completeOrder(
            @Valid @RequestBody @Parameter(description = "Details of the order to be completed") CompleteOrderRequest completeOrderRequest) {
        log.info("Received request to complete order: {}", completeOrderRequest);

        if (!completeOrderRequest.isOrderPaid()) {
            return orderService.processOrderNotPaid(completeOrderRequest.getOrderId())
                    .thenApply(voidResult -> {
                        log.info("Order payment processed as failed for order: {}", completeOrderRequest.getOrderId());
                        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Order marked as not paid", 200));
                    });
        }

        // Fetch client data needed to complete the order
        return orderService.bringClientDataToCompleteOrder(completeOrderRequest)
                .thenCompose(clientData -> {
                    log.debug("Client data fetched successfully: {}", clientData);

                    // Process order payment with the fetched client data
                    return orderService.processOrderPayment(completeOrderRequest, clientData.getAddressDTO(), clientData.getClientDTO(), clientData.getOrderDTO())
                            .thenApply(processResult -> {
                                // Check if the payment process was successful
                                if (!processResult.isSuccess()) {
                                    log.warn("Order payment processing failed: {}", processResult.getErrorMessage());
                                    // Return a bad request response with the error message
                                    return ResponseEntity.badRequest()
                                            .body(new ResponseWrapper<>(false, null, processResult.getErrorMessage(), 400));
                                }

                                log.info("Order payment processed successfully for order: {}", completeOrderRequest.getOrderId());
                                // Return a success response indicating the order is completed
                                return ResponseEntity.ok()
                                        .body(new ResponseWrapper<>(true, null, "Order completed", 200));
                            });
                });
    }



    @Operation(summary = "Add payment ID to an order", description = "Associates a payment ID with an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated with payment ID successfully."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @PutMapping("/{orderId}/payment/{paymentId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addPaymentIdByOrderId(
            @PathVariable @Parameter(description = "ID of the order to update") Long orderId,
            @PathVariable @Parameter(description = "ID of the payment to associate with the order") Long paymentId) {
        return CompletableFuture.runAsync(() -> orderService.addPaymentIdByOrderId(orderId, paymentId))
                .thenApply(v -> ResponseEntity.ok()
                        .body(new ResponseWrapper<>(true, null, "Order Updated", 200))
                );
    }



    @Operation(summary = "Get an order by ID", description = "Retrieves the details of an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @GetMapping("/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<OrderDTO>>> getOrderById(
            @PathVariable @Parameter(description = "ID of the order to retrieve") Long orderId) {
        return orderService.getOrderById(orderId)
                .thenApply(orderOpt -> orderOpt
                        .map(orderDTO -> ResponseEntity.ok()
                                .body(new ResponseWrapper<>(true, orderDTO, "Order successfully fetched.", 200)))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Order with ID " + orderId + " not found", 404)))
                );
    }


    @Operation(summary = "Deliver an order", description = "Updates the delivery status of an order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order delivery status updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid delivery status or order details."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @PutMapping("/deliver/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deliverOrder(
            @PathVariable @Parameter(description = "ID of the order to update") Long orderId,
            @RequestParam @Parameter(description = "Indicates if the order is delivered") boolean isDelivered) {
        return orderService.getOrderById(orderId)
                .thenCompose(orderOpt -> {
                    if (orderOpt.isEmpty()) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Order with ID " + orderId + " not found.", 404)));
                    }

                    return orderService.validateOrderForDelivery(orderOpt.get())
                            .thenCompose(validateResult -> {
                                if (!validateResult.isSuccess()) {
                                    return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                                            .body(new ResponseWrapper<>(false, null, validateResult.getErrorMessage(), 400)));
                                }

                                return orderService.deliveryOrder(orderId, isDelivered)
                                        .thenApply(deliveryStatus -> ResponseEntity.ok()
                                                .body(new ResponseWrapper<>(true, null, deliveryStatus, 200)));
                            });
                });
    }
}
