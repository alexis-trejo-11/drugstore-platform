package microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.request.ReceivePurchaseOrderRequest;
import microservice.inventory_service.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.order.supplier_purchase.application.query.*;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.order.supplier_purchase.domain.port.input.PurchaseOrderUseCase;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.request.InsertPurchaseOrderRequest;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.response.PurchaseOrderResponse;
import microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders/purchase")
public class PurchaseOrderController {
    private final PurchaseOrderUseCase purchaseOrderUsecase;
    private final EntityDetailMapper<PurchaseOrder, PurchaseOrderResponse> detailMapper;
    private final ResponseMapper<OrderSummaryResponse, PurchaseOrder> responseMapper;

    @PostMapping("/request")
    public ResponseWrapper<PurchaseOrderId> requestOrder(@Valid @RequestBody InsertPurchaseOrderRequest request) {
        var command = request.toCreateCommand();
        var orderId = purchaseOrderUsecase.insertOrder(command);
        return ResponseWrapper.created(orderId, "PurchaseOrder");
    }

    @PatchMapping("/{id}/status")
    public ResponseWrapper<Void> updateOrderStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        var command = new UpdatePurchaseOrderStatusCommand(PurchaseOrderId.of(id), status, null);
        purchaseOrderUsecase.updatePurchaseOrderStatus(command);
        return ResponseWrapper.updated(null, "PurchaseOrder");
    }

    @PutMapping("{id}/receive")
    public ResponseWrapper<PurchaseOrderId> receiveOrder(@PathVariable String id, @Valid  @RequestBody ReceivePurchaseOrderRequest request) {
        var command = request.toCommand(id);
        purchaseOrderUsecase.receiveOrder(command);
        return ResponseWrapper.success(null, "PurchaseOrder successfully received items");
    }

    @PutMapping("/{id}")
    public ResponseWrapper<Void> updateOrder(@Valid @RequestBody InsertPurchaseOrderRequest request, @PathVariable String id) {
        var command = request.toUpdateCommand(PurchaseOrderId.of(id));
        purchaseOrderUsecase.insertOrder(command);
        return ResponseWrapper.updated(null, "PurchaseOrder");
    }

    @GetMapping("/{id}")
    public ResponseWrapper<PurchaseOrderResponse> getOrderById(@PathVariable String id) {
        var query = GetOrderByIdQuery.of(id);
        PurchaseOrder purchaseOrder = purchaseOrderUsecase.getOrderById(query);

        PurchaseOrderResponse orderResponse = detailMapper.toDetail(purchaseOrder);
        return ResponseWrapper.found(orderResponse, "PurchaseOrder");
    }

    @GetMapping("supplier/{supplierId}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersBySupplierId(
            @PathVariable String supplierId,
            @Valid @ModelAttribute PageRequest pageRequest) {

        GetOrdersBySupplierIdQuery query = new GetOrdersBySupplierIdQuery(supplierId, pageRequest.toPageable());
        var purchaseOrderPage = purchaseOrderUsecase.getOrdersBySupplierId(query);

        var orderResponsePage = responseMapper.toResponsePage(purchaseOrderPage);
        return ResponseWrapper.found(orderResponsePage, "PurchaseOrder");
    }

    @GetMapping("status/{status}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByStatus(
            @Valid @ModelAttribute PageRequest pageRequest,
            @PathVariable OrderStatus status) {

        var query = new GetOrdersByStatusQuery(status, pageRequest.toPageable());
        Page<PurchaseOrder> orderPage = purchaseOrderUsecase.getOrdersByStatus(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "PurchaseOrder");
    }

    @GetMapping("expected_date/before/{date}")
    public ResponseWrapper<PageResponse<OrderSummaryResponse>> getOrdersByExpectedDateBefore(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date, @Valid @ModelAttribute PageRequest pageRequest) {
        var query = new GetOrderByExpectedDateBeforeQuery(date, pageRequest.toPageable());
        Page<PurchaseOrder> orderPage = purchaseOrderUsecase.getOrdersByExpectedDateBefore(query);

        var orderResponsePage = responseMapper.toResponsePage(orderPage);
        return ResponseWrapper.found(orderResponsePage, "PurchaseOrder");
    }
}