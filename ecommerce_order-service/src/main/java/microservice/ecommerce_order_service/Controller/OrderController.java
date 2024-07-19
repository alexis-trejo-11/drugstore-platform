package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.OrderService;
import microservice.ecommerce_order_service.Service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("v1/api/orders")
@RateLimiter(name = "orderApiLimiter")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/complete-order")
    public ResponseEntity<ApiResponse<?>> completeOrder(@Valid @RequestBody CompleteOrderRequest completeOrderRequest) {
        log.info("Received request to complete order: {}", completeOrderRequest);

        var clientData = orderService.bringClientDataToCompleteOrder(completeOrderRequest);
        log.debug("Client data fetched successfully: {}", clientData);

        orderService.processOrderPayment(completeOrderRequest, clientData.getAddressDTO(), clientData.getClientDTO(), clientData.getOrderDTO());
        log.info("Order payment processed successfully for order: {}", completeOrderRequest.getOrderId());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Order completed", 200));
    }

    @PutMapping("/{orderId}/payment/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> addPaymentIdByOrderId(@Valid @PathVariable Long orderId, @PathVariable Long paymentId) {
        orderService.addPaymentIdByOrderId(orderId, paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Order Updated", 200));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        if (orderDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found",404));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, orderDTO, "Orders successfully fetched.", 200));
    }

    @PutMapping("/deliver/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deliverOrder(@PathVariable Long orderId, @RequestParam boolean isDelivered) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        if (orderDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Order with ID " + orderId.toString() + " not found.", 404 ));
        }

        Result<Void> validateResult = orderService.validateOrderForDelivery(orderDTO);
        if (!validateResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, validateResult.getErrorMessage(), 400));
        }

        String deliveryStatus = orderService.deliveryOrder(orderId, isDelivered);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, deliveryStatus, 200));
    }

}
