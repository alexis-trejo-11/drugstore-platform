package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.query.GetOrderByExpectedDateBeforeQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByIdQuery;
import microservice.inventory_service.external.order.application.query.GetOrderByNumberQuery;
import microservice.inventory_service.external.order.application.query.GetOrdersByStatusQuery;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderStatus;
import microservice.inventory_service.external.order.domain.port.input.OrderQueryService;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.OrderDetailResponse;
import microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderQueryService orderQueryService;
    private final EntityDetailMapper<PurchaseOrder, OrderDetailResponse> detailMapper;
    private final ResponseMapper<OrderSummaryResponse, PurchaseOrder> responseMapper;

    @GetMapping("/{id}")
    public ResponseWrapper<OrderDetailResponse> getOrderById(@PathVariable String id) {
        var query = GetOrderByIdQuery.of(id);
        PurchaseOrder order = orderQueryService.getOrderById(query);

        OrderDetailResponse orderResponse = detailMapper.toDetail(order);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @GetMapping("order_number/{orderNumber}")
    public ResponseWrapper<OrderDetailResponse> getOrderByNumber(@PathVariable String orderNumber) {
        var query = new GetOrderByNumberQuery(orderNumber);
        PurchaseOrder order = orderQueryService.getOrderByNumber(query);

        OrderDetailResponse orderResponse = detailMapper.toDetail(order);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @GetMapping("status/{status}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByStatus(@PathVariable PurchaseOrderStatus status,
                                                                                 @Valid @ModelAttribute PageRequest pageRequest) {
        var query = new GetOrdersByStatusQuery(status, pageRequest.toPageable());
        Page<PurchaseOrder> orderPage = orderQueryService.getOrdersByStatus(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "Order");
    }

    @GetMapping("expected_date/before/{date}")
    public  ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByExpectedDateBefore(@PathVariable LocalDateTime date,
                                                                                              @Valid @ModelAttribute PageRequest pageRequest) {
        var query = new GetOrderByExpectedDateBeforeQuery(date, pageRequest.toPageable());
        Page<PurchaseOrder> orderPage = orderQueryService.getOrdersByExpectedDateBefore(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "Order");
    }
}