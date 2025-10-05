package microservice.order_service.infrastructure.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.DeleteOrderCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.queries.request.GetOrderByIdQuery;
import microservice.order_service.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.infrastructure.api.controller.dto.CreateOrderRequest;
import microservice.order_service.infrastructure.api.controller.dto.OrderResponse;
import microservice.order_service.infrastructure.api.controller.mapper.OrderResponseMapper;
import microservice.order_service.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders")
public class AdminOrdersController {
    private final OrderApplicationFacade orderService;
    private final OrderResponseMapper responseMapper;

    @GetMapping("/{id}")
    private ResponseWrapper<OrderResponse> getOrderByID(@NotNull @PathVariable  String id) {
        var query = GetOrderByIdQuery.of(id);
        var queryResult = orderService.getOrderById(query);

        var orderResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @PostMapping
    private ResponseWrapper<CreateOrderCommandResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var command = request.toCommand();
        var result = orderService.createOrder(command);

        return ResponseWrapper.created(result ,"Order");
    }

    @PutMapping("/{id}/cancel")
    private ResponseWrapper<CancelOrderCommandResponse> cancelOrder(@PathVariable String orderId, @RequestParam String reason) {
        var command = CancelOrderCommand.adminCancel(orderId, reason);
        var queryResult = orderService.cancelOrder(command);

        return ResponseWrapper.success(queryResult, "Order Successfully Canceled");
    }

    @DeleteMapping("/{id}")
    private ResponseWrapper<Void> deleteOrder(String orderId, @RequestParam boolean isHard) {
        var command = isHard ? DeleteOrderCommand.hardDelete(orderId)
                : DeleteOrderCommand.softDelete(orderId);

        orderService.deleteOrder(command);
        return ResponseWrapper.success("Order Successfully Deleted");
    }

}
