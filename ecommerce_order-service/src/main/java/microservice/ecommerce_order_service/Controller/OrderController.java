package microservice.ecommerce_order_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Service.OrderService;
import microservice.ecommerce_order_service.Service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO, BindingResult bindingResult) {
        log.info("Received create order request: {}", orderInsertDTO);
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationErrors, "Validation errors.", 400));
        }

        OrderDTO orderDTO = orderService.createOrder(orderInsertDTO);
        log.info("Order created successfully with ID: {}", orderDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, orderDTO, "Order created successfully created.", 201));
    }



    @PutMapping("/complete-order")
    public ResponseEntity<ApiResponse<Void>> completeOrder(@RequestParam boolean isOrderPaid, @RequestParam Long orderId)
    {
        Result<Void> orderResult = orderService.validateOrderPayment(isOrderPaid, orderId);
        if (!orderResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found",404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, null, "Order completed",200));
    }

    @Cacheable(value = "orderCache", key = "#clientId + '-' + #page + '-' + #size")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getOrdersByClientId(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        boolean isClientValidate = orderService.validateExistingClient(clientId);
        if (!isClientValidate) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Client with ID " + clientId + " not found",404));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orderDTOS = orderService.getOrdersByClientId(clientId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, orderDTOS, "Orders successfully fetched.", 200));
    }



    /**
     * Asynchronously delivers an order based on orderId and isDelivered flag.
     * Handles order delivery process and returns appropriate HTTP response.
     *
     * @param orderId The ID of the order to be delivered.
     * @param isDelivered Flag indicating whether the order is successfully delivered.
     * @return ResponseEntity<ApiResponse<String>>> A holding the delivery response.
     */
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


    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
       OrderDTO orderDTO = orderService.getOrderById(orderId);
       if (orderDTO == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Order with ID " + orderId + " not found.", 400));

       }

        Result<Void> orderResult = orderService.cancelOrder(orderId);
        if (!orderResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, orderResult.getErrorMessage(), 400));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Order successfully canceled.", 200));
    }
}
