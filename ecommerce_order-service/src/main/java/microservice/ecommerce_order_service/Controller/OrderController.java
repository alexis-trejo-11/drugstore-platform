package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Models.Sales.Sale;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import javax.swing.text.html.Option;
import java.util.Optional;
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
    public ResponseEntity<ResponseWrapper<OrderDTO>> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO) {
        log.info("Received create order request: {}", orderInsertDTO);
        var order = orderService.createOrder(orderInsertDTO);

        log.info("Order created successfully with ID: {}", order.getId());
        OrderDTO orderDTO = orderService.processOrderCreation(order, orderInsertDTO.getClientId(), orderInsertDTO.getCartDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(orderDTO, "Order"));
    }

    @Operation(summary = "Complete an order", description = "Completes an order by processing payment and updating the order status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as completed successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to process payment or invalid order details.")
    })
    @PutMapping("/complete-order")
    public ResponseEntity<ResponseWrapper<Void>> completeOrder(@Valid @RequestBody CompleteOrderRequest completeOrderRequest) {
        log.info("Received request to complete order: {}", completeOrderRequest);

        if (!completeOrderRequest.isOrderPaid()) {
            orderService.processOrderNotPaid(completeOrderRequest.getOrderId());
            log.info("Order payment processed as failed for order: {}", completeOrderRequest.getOrderId());
            return ResponseEntity.ok(ResponseWrapper.success("Order marked as not paid"));
        }

        // Fetch client data needed to complete the order
        var clientDataFuture = orderService.bringClientDataToCompleteOrder(completeOrderRequest);
        var clientData = clientDataFuture.join();

        log.debug("Client data fetched successfully: {}", clientData);

        // Process order payment with the fetched client data
        Result<Void> processResult = orderService.processOrderPayment(completeOrderRequest,
                clientData.getAddressDTO(),
                clientData.getClientDTO(),
                clientData.getOrderDTO());

        // Check if the payment process was successful
        if (!processResult.isSuccess()) {
            log.warn("Order payment processing failed: {}", processResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(processResult.getErrorMessage()));
        }

        log.info("Order payment processed successfully for order: {}", completeOrderRequest.getOrderId());

        return ResponseEntity.ok(ResponseWrapper.success("Order completed"));
    }

    @Operation(summary = "Add payment ID to an order", description = "Associates a payment ID with an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated with payment ID successfully."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @PutMapping("/{orderId}/payment/{paymentId}")
    public ResponseEntity<ResponseWrapper<Void>> addPaymentIdByOrderId(@PathVariable Long orderId,
                                                                                          @PathVariable Long paymentId) {
       orderService.addPaymentIdByOrderId(orderId, paymentId);
       return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Order Updated", 200));
    }

    @Operation(summary = "Get an order by ID", description = "Retrieves the details of an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrderById(@PathVariable Long orderId) {
        Optional<OrderDTO> optionalOrderDTO = orderService.getOrderById(orderId);

        return optionalOrderDTO.map(orderDTO -> ResponseEntity.ok(ResponseWrapper.found(orderDTO, "Order")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Order", "Id")));

    }

    @Operation(summary = "Deliver an order", description = "Updates the delivery status of an order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order delivery status updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid delivery status or order details."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @PutMapping("/deliver/{orderId}")
    public ResponseEntity<ResponseWrapper<Void>> deliverOrder(@PathVariable Long orderId,
                                                              @RequestParam boolean isDelivered) {
        Optional<OrderDTO> optionalOrderDTO = orderService.getOrderById(orderId);
        if (optionalOrderDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Order", "Id"));
        }

        Result<Void> validateResult = orderService.validateOrderForDelivery(optionalOrderDTO.get());
        if (!validateResult.isSuccess()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.badRequest(validateResult.getErrorMessage()));
        }

        String orderStatus = orderService.deliveryOrder(orderId, isDelivered);
        return ResponseEntity.ok(ResponseWrapper.success(orderStatus));
    }
}
