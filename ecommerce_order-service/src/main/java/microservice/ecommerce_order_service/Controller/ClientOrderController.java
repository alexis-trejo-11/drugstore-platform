package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.ClientOrderService;
import microservice.ecommerce_order_service.Service.OrderService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Slf4j
@RestController
@RequestMapping("v1/api/client-orders")
public class ClientOrderController {

    private final ClientOrderService clientOrderService;
    private final OrderService orderService;

    public ClientOrderController(ClientOrderService clientOrderService, OrderService orderService) {
        this.clientOrderService = clientOrderService;
        this.orderService = orderService;
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get cancelled orders by client ID",
            description = "Retrieve a paginated list of cancelled orders for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cancelled orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/current/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Page<OrderDTO>>>> getCancelledOrdersByClientId(
            @Parameter(description = "ID of the client to retrieve cancelled orders for") @PathVariable Long clientId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        return clientOrderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return clientOrderService.getCancelledOrdersByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ResponseWrapper<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }



    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get orders to be delivered by client ID",
            description = "Retrieve a paginated list of orders to be delivered for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders to be delivered"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/delivering/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Page<OrderDTO>>>> getOrdersToBeDeliveredByClientId(
            @Parameter(description = "ID of the client to retrieve orders to be delivered for") @PathVariable Long clientId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        return clientOrderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return clientOrderService.getCurrentOrdersByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ResponseWrapper<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }



    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get completed orders by client ID",
            description = "Retrieve a paginated list of completed orders for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved completed orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/completed/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Page<OrderDTO>>>> getCompletedOrdersByClientId(
            @Parameter(description = "ID of the client to retrieve completed orders for") @PathVariable Long clientId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        return clientOrderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return clientOrderService.getCompletedOrdersByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ResponseWrapper<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }



    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get pending payment orders by client ID",
            description = "Retrieve a paginated list of orders pending payment for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending payment orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/pending/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Page<OrderDTO>>>> getPendingPaymentOrdersByClientId(
            @Parameter(description = "ID of the client to retrieve pending payment orders for") @PathVariable Long clientId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        return clientOrderService.validateExistingClient(clientId)
                .thenCompose(isClientValidate -> {
                    if (!isClientValidate) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client with ID " + clientId + " not found", 404)));
                    }

                    Pageable pageable = PageRequest.of(page, size);
                    return clientOrderService.getOrdersToBeValidatedByClientId(clientId, pageable)
                            .thenApply(orderDTOS -> ResponseEntity.status(HttpStatus.OK)
                                    .body(new ResponseWrapper<>(true, orderDTOS, "Orders successfully fetched.", 200)));
                });
    }



    @Operation(summary = "Cancel an order by ID",
            description = "Cancel a specific order based on the provided order ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully cancelled the order"),
            @ApiResponse(responseCode = "400", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Conflict occurred while cancelling the order")
    })
    @GetMapping("/cancel/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> cancelOrder(
            @Parameter(description = "ID of the order to be cancelled") @PathVariable Long orderId) {

        return orderService.getOrderById(orderId)
                .thenCompose(optionalOrderDTO -> {
                    if (optionalOrderDTO.isEmpty()) {
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Order with ID " + orderId + " not found.", 400)));
                    }

                    return clientOrderService.cancelOrder(orderId)
                            .thenApply(orderResult -> {
                                if (!orderResult.isSuccess()) {
                                    return ResponseEntity.status(HttpStatus.CONFLICT)
                                            .body(new ResponseWrapper<>(false, null, orderResult.getErrorMessage(), 409));
                                }

                                return ResponseEntity.status(HttpStatus.OK)
                                        .body(new ResponseWrapper<>(true, null, "Order successfully canceled.", 200));
                            });
                });
    }
}
