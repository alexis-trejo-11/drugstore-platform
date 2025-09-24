package microservice.order_service.infrastructure.api.controller;


import libs_kernel.response.ResponseWrapper;
import microservice.order_service.infrastructure.api.controller.dto.OrderDetailResponse;
import microservice.order_service.infrastructure.api.controller.dto.OrderResponse;
import microservice.order_service.infrastructure.api.controller.dto.OrderSummaryResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/customers/orders")
public class CustomerOrderController {

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<OrderResponse>>> getCustomerOrders(
            @PathVariable String customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        OrderQuery query = OrderQuery.builder()
                .customerId(customerId)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        List<OrderResponse> orders = orderQueryService.findOrdersByCustomer(query);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getCustomerOrderDetail(
            @PathVariable String customerId,
            @PathVariable String orderId) {

        OrderDetailResponse order = orderQueryService.findOrderDetail(customerId, orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<OrderSummaryResponse>> getRecentOrders(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "5") int limit) {

        List<OrderSummaryResponse> recentOrders = orderQueryService.findRecentOrders(customerId, limit);
        return ResponseEntity.ok(recentOrders);
    }
}

}
