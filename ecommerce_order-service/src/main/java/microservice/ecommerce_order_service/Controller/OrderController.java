package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect validation errors
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            // Create a ResponseWrapper with the validation errors
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, String.join(", ", errorMessages), HttpStatus.BAD_REQUEST);

            // Log validation errors
            log.error("Validation errors: {}", errorMessages);

            // Return a BadRequest response with the error details
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
        }

        // If validation was successful, proceed to create the order
        return orderService.createOrder(orderInsertDTO)
                .thenApply(createOrderResult -> {
                    // Check if the order creation was successful
                    if (!createOrderResult.isSuccess()) {
                        // Return a response with an error if order creation failed
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, createOrderResult.getErrorMessage(), createOrderResult.getStatus());
                        return ResponseEntity.status(createOrderResult.getStatus()).body(errorResponse);
                    } else {
                        // All operations succeeded, return a success response
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Order Successfully Created! Payment will be validated soon.", HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> {
                    // Log the exception
                    log.error("Exception occurred while creating order", ex);
                    // Handle exceptions and return an internal server error response
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }



    @PutMapping("/complete-order")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> completeOrder(
            @RequestParam boolean isOrderPaid,
            @RequestParam Long orderId)
    {
        return orderService.validateOrderPayment(isOrderPaid, orderId)
                .thenCompose(orderResult -> {
                    if (!orderResult.isSuccess()) {
                        // Payment validation failed
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, orderResult.getErrorMessage(), orderResult.getStatus());
                        return CompletableFuture.completedFuture(ResponseEntity.status(orderResult.getStatus()).body(errorResponse));
                    }

                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, orderResult.getErrorMessage(), orderResult.getStatus());
                    return CompletableFuture.completedFuture(ResponseEntity.status(orderResult.getStatus()).body(errorResponse));
                })
                .exceptionally(e -> {
                    // Handle unexpected exceptions
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }


    @GetMapping("/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<OrderDTO>>> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<OrderDTO> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

                    } else {
                        ResponseWrapper<OrderDTO> response = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<OrderDTO> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<OrderDTO>>>> getOrdersByClientId(@PathVariable Long clientId) {
        return orderService.getOrdersByClientId(clientId)
                .thenApply(listResult -> {
                    ResponseWrapper<List<OrderDTO>> response = new ResponseWrapper<>(listResult.getData(), null);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<OrderDTO>> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Asynchronously delivers an order based on orderId and isDelivered flag.
     * Handles order delivery process and returns appropriate HTTP response.
     *
     * @param orderId The ID of the order to be delivered.
     * @param isDelivered Flag indicating whether the order is successfully delivered.
     * @return CompletableFuture<ResponseEntity<ResponseWrapper<String>>> A CompletableFuture holding the delivery response.
     */
    @PutMapping("/deliver/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<String>>> deliverOrder(@PathVariable Long orderId, @RequestParam boolean isDelivered) {
        // Invoke deliveryOrder service method asynchronously
        return orderService.deliveryOrder(orderId, isDelivered)
                .thenApply(result -> {
                    // Handle case where deliveryOrder service method returns an error result
                    if (!result.isSuccess()) {
                        if (result.getStatus() == HttpStatus.NOT_FOUND) {
                            // Return 404 Not Found response for order not found
                            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), result.getStatus());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                        } else if (result.getStatus() == HttpStatus.BAD_REQUEST) {
                            // Return 400 Bad Request response for validation or business rule failure
                            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), result.getStatus());
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                        }
                    }

                    // Return success response with delivery status
                    ResponseWrapper<String> successResponse = new ResponseWrapper<>(result.getData(), null, result.getStatus());
                    return ResponseEntity.ok(successResponse);
                })
                .exceptionally(ex -> {
                    // Handle unexpected exceptions during the delivery process
                    ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }


    @PutMapping("/cancel/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<Void> successResponse = new ResponseWrapper<>(null, null, result.getStatus());
                        return ResponseEntity.ok(successResponse);
                    } else {
                        HttpStatus status = result.getStatus() != null ? result.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), status);
                        return ResponseEntity.status(status).body(errorResponse);
                    }
                })
                .exceptionally(ex -> {
                    String errorMessage = "An unexpected error occurred: " + ex.getMessage();
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

}
