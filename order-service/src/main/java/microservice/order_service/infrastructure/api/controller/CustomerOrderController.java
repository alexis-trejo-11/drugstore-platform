package microservice.order_service.infrastructure.api.controller;


import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.request.GetOrderByCustomerAndIdQuery;
import microservice.order_service.application.queries.request.GetRecentOrdersByCustomerQuery;
import microservice.order_service.application.queries.response.OrderQueryResult;
import microservice.order_service.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.infrastructure.api.controller.dto.GetCustomerOrders;
import microservice.order_service.infrastructure.api.controller.dto.OrderDetailResponse;
import microservice.order_service.infrastructure.api.controller.dto.OrderResponse;
import microservice.order_service.infrastructure.api.controller.dto.PagedOrderResponse;
import microservice.order_service.infrastructure.api.controller.mapper.OrderResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/customers/orders")
@RequiredArgsConstructor
public class CustomerOrderController {
    private final OrderApplicationFacade orderService;
    private final OrderResponseMapper mapper;


    @GetMapping("/{customerId}")
    public ResponseWrapper<PagedOrderResponse> getCustomerOrders(
            @ModelAttribute GetCustomerOrders request,
            @PathVariable String customerId
    ) {
        var query = request.toQuery(customerId);
        var resultPage = orderService.getOrdersByCustomer(query);

        var ordersPaged = mapper.toPagedResponse(resultPage);
        return ResponseWrapper.found(ordersPaged, "Orders");
    }

    @GetMapping("/{orderId}/{customerId}")
    public ResponseWrapper<OrderDetailResponse> getCustomerOrderDetail(
            @PathVariable String customerId,
            @PathVariable String orderId) {

       var query = GetOrderByCustomerAndIdQuery.of(customerId, orderId);
       var response = orderService.getOrderByCustomerAndId(query);

       var orderResponse = mapper.toDetailResponse(response);
       return ResponseWrapper.found(orderResponse, "Order");
    }
}
