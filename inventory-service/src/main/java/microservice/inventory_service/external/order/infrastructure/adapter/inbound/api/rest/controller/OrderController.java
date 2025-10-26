package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.external.order.domain.port.input.OrderUseCase;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.request.InsertOrderRequest;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderDetailResponse;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderUseCase orderUsecase;
    private final EntityDetailMapper<Order, OrderDetailResponse> detailMapper;
    private final ResponseMapper<OrderSummaryResponse, Order> responseMapper;

    @PostMapping
    public ResponseWrapper<OrderId> createOrder (@Valid @RequestBody InsertOrderRequest request) {
        var command = request.toCreateCommand();
        var orderId = orderUsecase.insertOrder(command);
        return ResponseWrapper.created(orderId, "Order");
    }

    @PutMapping("/{id}")
    public ResponseWrapper<OrderId> updateOrder(@Valid @RequestBody InsertOrderRequest request, @PathVariable String id) {
        var command = request.toUpdateCommand(OrderId.of(id));
        var orderId = orderUsecase.insertOrder(command);
        return ResponseWrapper.created(orderId, "Order");
    }

    @GetMapping("/{id}")
    public ResponseWrapper<OrderDetailResponse> getOrderById(@PathVariable String id) {
        var query = GetOrderByIdQuery.of(id);
        Order order = orderUsecase.getOrderById(query);

        OrderDetailResponse orderResponse = detailMapper.toDetail(order);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @GetMapping("order_number/{orderNumber}")
    public ResponseWrapper<OrderDetailResponse> getOrderByNumber(@PathVariable String orderNumber) {
        var query = new GetOrderByNumberQuery(orderNumber);
        Order order = orderUsecase.getOrderByNumber(query);

        OrderDetailResponse orderResponse = detailMapper.toDetail(order);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @GetMapping("status/{status}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByStatus(@PathVariable OrderStatus status,
                                                                                 @Valid @ModelAttribute PageRequest pageRequest) {
        var query = new GetOrdersByStatusQuery(status, pageRequest.toPageable());
        Page<Order> orderPage = orderUsecase.getOrdersByStatus(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "Order");
    }

    @GetMapping("expected_date/before/{date}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByExpectedDateBefore(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @Valid @ModelAttribute PageRequest pageRequest) {
        log.info("Fetching pagination info: page {}, size {}", pageRequest.getPage(), pageRequest.getSize());
        var query = new GetOrderByExpectedDateBeforeQuery(date, pageRequest.toPageable());
        Page<Order> orderPage = orderUsecase.getOrdersByExpectedDateBefore(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "Order");
    }
}