package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/client-orders")
public class ClientOrderController {

    private final ClientOrderService clientOrderService;
    private final OrderService orderService;
    private final AuthSecurity authSecurity;

    public ClientOrderController(ClientOrderService clientOrderService,
                                 OrderService orderService,
                                 AuthSecurity authSecurity) {
        this.clientOrderService = clientOrderService;
        this.orderService = orderService;
        this.authSecurity = authSecurity;
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get cancelled orders by client ID",
            description = "Retrieve a paginated list of cancelled orders for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cancelled orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/cancelled")
    public ResponseEntity<ResponseWrapper<Page<OrderDTO>>> getCancelledOrdersByClientId(HttpServletRequest request,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "10") int size) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getCancelledOrdersByClientId -> Fetching cancelled orders for client Id: {}", clientId);

        CompletableFuture<Boolean> validatedExistingClientFuture  = clientOrderService.validateExistingClient(clientId);
        Boolean isClientValidated = validatedExistingClientFuture.join();

        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Client", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orderDTOS = clientOrderService.getCancelledOrdersByClientId(clientId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, orderDTOS, "Orders successfully fetched.", 200));
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get orders to be delivered by client ID",
            description = "Retrieve a paginated list of orders to be delivered for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders to be delivered"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/delivering")
    public ResponseEntity<ResponseWrapper<Page<OrderDTO>>> getOrdersToBeDeliveredByClientId(HttpServletRequest request,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getOrdersToBeDeliveredByClientId -> Fetching cancelled orders for client Id: {}", clientId);

        CompletableFuture<Boolean> validatedExistingClientFuture  = clientOrderService.validateExistingClient(clientId);
        Boolean isClientValidated = validatedExistingClientFuture.join();

        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Client", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO>  orderDTOS = clientOrderService.getCurrentOrdersByClientId(clientId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(orderDTOS, "Orders"));
    }


    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get completed orders by client ID",
            description = "Retrieve a paginated list of completed orders for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved completed orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/completed")
    public ResponseEntity<ResponseWrapper<Page<OrderDTO>>> getCompletedOrdersByClientId(HttpServletRequest request,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "10") int size) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getCompletedOrdersByClientId -> Fetching cancelled orders for client Id: {}", clientId);

        CompletableFuture<Boolean> validatedExistingClientFuture  = clientOrderService.validateExistingClient(clientId);
        Boolean isClientValidated = validatedExistingClientFuture.join();

        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Client", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orderDTOS = clientOrderService.getCompletedOrdersByClientId(clientId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(orderDTOS,"Orders"));
    }


    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @Operation(summary = "Get pending payment orders by client ID",
            description = "Retrieve a paginated list of orders pending payment for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending payment orders"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/pending")
    public ResponseEntity<ResponseWrapper<Page<OrderDTO>>> getPendingPaymentOrdersByClientId(HttpServletRequest request,
                                                                                             @RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "10") int size) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getPendingPaymentOrdersByClientId -> Fetching cancelled orders for client Id: {}", clientId);

        CompletableFuture<Boolean> validatedExistingClientFuture  = clientOrderService.validateExistingClient(clientId);
        Boolean isClientValidated = validatedExistingClientFuture.join();

        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Client", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orderDTOS = clientOrderService.getOrdersToBeValidatedByClientId(clientId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(orderDTOS,"Orders"));
    }

    @Operation(summary = "Cancel an order by ID",
            description = "Cancel a specific order based on the provided order ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully cancelled the order"),
            @ApiResponse(responseCode = "400", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Conflict occurred while cancelling the order")
    })
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseWrapper<Void>> cancelOrder(@PathVariable Long orderId) {
        Optional<OrderDTO> optionalOrderDTO = orderService.getOrderById(orderId);
        if (optionalOrderDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Order", "Id"));
        }

        Result<Void> orderResult = clientOrderService.cancelOrder(orderId);
        if (!orderResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseWrapper.error(orderResult.getErrorMessage(), 409));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Order", "Cancel"));

    }
}
