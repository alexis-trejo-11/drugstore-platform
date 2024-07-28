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
@RequestMapping("v1/api/client-orders")
@RateLimiter(name = "orderApiLimiter")
public class ClientOrderController {
    private final OrderService orderService;

    @Autowired
    public ClientOrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO) {
        log.info("Received create order request: {}", orderInsertDTO);
        var order = orderService.createOrder(orderInsertDTO);
        log.info("Order created successfully with ID: {}", order.getId());

        OrderDTO orderDTO = orderService.processOrderCreation(order, orderInsertDTO.getClientId(), orderInsertDTO.getCartDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, orderDTO, "Order created successfully created.", 201));
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
