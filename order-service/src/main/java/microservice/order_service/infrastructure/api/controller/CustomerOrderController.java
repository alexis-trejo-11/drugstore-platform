package microservice.order_service.infrastructure.api.controller;


import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.response.OrderQueryResponse;
import microservice.order_service.application.queries.response.OrderSummaryQueryResponse;
import microservice.order_service.application.queries.response.PagedOrderSummaryQueryResponse;
import microservice.order_service.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.infrastructure.api.controller.dto.GetCustomerOrders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/customers/orders")
@RequiredArgsConstructor
public class CustomerOrderController {
    private final OrderApplicationFacade orderService;


    @GetMapping("/{customerId}")
    public ResponseWrapper<PagedOrderSummaryQueryResponse> getCustomerOrders(
            @ModelAttribute GetCustomerOrders query,
            @PathVariable String customerId) {

        var orders = orderService.getOrdersByCustomer(
                customerId,
                query.getPage(),
                query.getSize());

        return ResponseWrapper.found(orders, "Orders");
    }

    @GetMapping("/{orderId}/{customerId}")
    public ResponseWrapper<OrderQueryResponse> getCustomerOrderDetail(
            @PathVariable String customerId,
            @PathVariable String orderId) {

        var response = orderService.getOrderByCustomerAndId(customerId, orderId);

        return ResponseWrapper.found(response, "Order");
    }

    @GetMapping("/recent")
    public ResponseEntity<List<OrderSummaryQueryResponse>> getRecentOrders(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "5") int limit) {

         var recentOrders = orderService.getRecentOrdersByCustomer(customerId, limit);
        return ResponseEntity.ok(recentOrders);
    }
}
