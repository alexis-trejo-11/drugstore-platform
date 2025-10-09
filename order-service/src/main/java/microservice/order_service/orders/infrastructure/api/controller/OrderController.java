package microservice.order_service.orders.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.page.PageableResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.queries.request.GetOrderByIDQuery;
import microservice.order_service.orders.application.queries.request.GetOrderDetailByIDQuery;
import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.dto.request.CreateOrderRequest;
import microservice.order_service.orders.infrastructure.api.dto.request.OrderSearchRequest;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders")
public class OrderController {
    private final OrderApplicationFacade orderService;
    private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
    private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;


    @Operation(summary = "Search orders with filters", description = "Search orders with multiple filter criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @GetMapping("/search")
    public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
            @Parameter(description = "Search criteria")
            @Valid OrderSearchRequest request) {

        SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
        Page<OrderQueryResult> resultPage = orderService.searchOrders(query);

        PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
        return ResponseWrapper.success(response, "Orders found successfully");
    }


    @GetMapping("/{id}")
    private ResponseWrapper<OrderResponse> getOrderByID(@NotNull @PathVariable  String id) {
        var query = GetOrderByIDQuery.of(id);
        var queryResult = orderService.getOrderByID(query);

        var orderResponse = mapper.toResponse(queryResult);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @GetMapping("/{id}/detail")
    private ResponseWrapper<OrderDetailResponse> getOrderDetailByID(@NotNull @PathVariable String id) {
        var query = GetOrderDetailByIDQuery.of(id);
        var queryResult = orderService.getOrderByID(query);
        var orderResponse = detailMapper.toDetail(queryResult);
        return ResponseWrapper.found(orderResponse, "Order Detail");
    }

    @PostMapping
    private ResponseWrapper<CreateOrderCommandResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var command = request.toCommand();
        var result = orderService.createOrder(command);
        return ResponseWrapper.created(result ,"Order");
    }

    @DeleteMapping("/{id}")
    private ResponseWrapper<Void> deleteOrder(@PathVariable String id, @RequestParam(defaultValue = "false") boolean isHard) {
        var command = isHard ? DeleteOrderCommand.hardDelete(id)
                : DeleteOrderCommand.softDelete(id);

        orderService.deleteOrder(command);
        return ResponseWrapper.success("Order Successfully Deleted");
    }

}
