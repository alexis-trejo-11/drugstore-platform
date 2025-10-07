package microservice.order_service.orders.infrastructure.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.CancelOrderCommand;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.queries.request.GetOrderByIDQuery;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.controller.dto.CreateOrderRequest;
import microservice.order_service.orders.infrastructure.api.controller.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders")
public class AdminOrdersController {
    private final OrderApplicationFacade orderService;
    private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;

    @GetMapping("/{id}")
    private ResponseWrapper<OrderResponse> getOrderByID(@NotNull @PathVariable  String id) {
        var query = GetOrderByIDQuery.of(id);
        var queryResult = orderService.getOrderByID(query);

        var orderResponse = mapper.toResponse(queryResult);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @PostMapping
    private ResponseWrapper<CreateOrderCommandResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var command = request.toCommand();
        var result = orderService.createOrder(command);

        return ResponseWrapper.created(result ,"Order");
    }

    @PutMapping("/{id}/cancel")
    private ResponseWrapper<CancelOrderCommandResponse> cancelOrder(@PathVariable String id, @RequestParam String reason) {
        var command = CancelOrderCommand.adminCancel(id, reason);
        var queryResult = orderService.cancelOrder(command);

        return ResponseWrapper.success(queryResult, "Order Successfully Canceled");
    }

    @DeleteMapping("/{id}")
    private ResponseWrapper<Void> deleteOrder(@PathVariable String id, @RequestParam(defaultValue = "false") boolean isHard) {
        var command = isHard ? DeleteOrderCommand.hardDelete(id)
                : DeleteOrderCommand.softDelete(id);

        orderService.deleteOrder(command);
        return ResponseWrapper.success("Order Successfully Deleted");
    }

}
