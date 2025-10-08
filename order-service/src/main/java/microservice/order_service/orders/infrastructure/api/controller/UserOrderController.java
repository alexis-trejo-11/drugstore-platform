package microservice.order_service.orders.infrastructure.api.controller;


import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

import microservice.order_service.orders.application.queries.request.GetOrderByIDAndUserIDQuery;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.dto.GetUserOrdersRequest;
import microservice.order_service.orders.infrastructure.api.dto.OrderDetailResponse;
import microservice.order_service.orders.infrastructure.api.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/customers/orders")
@RequiredArgsConstructor
public class UserOrderController {
    private final OrderApplicationFacade orderService;
    private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
    private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;


    @GetMapping("/{userID}")
    public ResponseWrapper<PageResponse<OrderResponse>> getUserOrders(
            @ModelAttribute GetUserOrdersRequest request,
            @PathVariable String customerId
    ) {
        var query = request.toQuery(customerId);
        var resultPage = orderService.getOrdersByUserID(query);

        var ordersPaged = mapper.toResponsePage(resultPage);
        return ResponseWrapper.found(ordersPaged, "Orders");
    }

    @GetMapping("/{orderID}/{userID}")
    public ResponseWrapper<OrderDetailResponse> getUserOrderDetail(
            @PathVariable String customerId,
            @PathVariable String orderId) {

       var query = GetOrderByIDAndUserIDQuery.of(customerId, orderId);
       var response = orderService.getOrderByIDAndUserID(query);

       var orderResponse = detailMapper.toDetail(response);
       return ResponseWrapper.found(orderResponse, "Order");
    }
}
