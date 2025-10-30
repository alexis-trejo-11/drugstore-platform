package microservice.inventory_service.order.sales.infrastructure.adapter.inbound.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.sales.core.application.command.CancelSaleOrderCommand;
import microservice.inventory_service.order.sales.core.application.command.ConfirmSaleOrderCommand;
import microservice.inventory_service.order.sales.core.application.command.FullFillOrderCommand;
import microservice.inventory_service.order.sales.core.application.command.SendSaleOrderToDeliveryCommand;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersById;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.core.domain.port.SalesOrderUseCase;
import microservice.inventory_service.order.sales.infrastructure.adapter.inbound.rest.dto.CreateSalesOrderRequest;
import microservice.inventory_service.order.sales.infrastructure.adapter.inbound.rest.dto.SalesOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/orders/sales")
@RequiredArgsConstructor
public class SalesOrderController {
    private final SalesOrderUseCase salesOrderUseCase;
    private final ResponseMapper<SalesOrderResponse, SaleOrder> responseMapper;

    @GetMapping("/{id}")
    public ResponseWrapper<SalesOrderResponse> getSalesOrderById(@PathVariable String id) {
        var query = GetSaleOrdersById.of(id);
        var order = salesOrderUseCase.getSalesOrderById(query);

        var response = responseMapper.toResponse(order);
        return ResponseWrapper.found(response, "SaleOrder");
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createSalesOrder(@Valid @RequestBody CreateSalesOrderRequest request) {
        var command = request.toCommand();
        salesOrderUseCase.receiveSaleOrder(command);

        var response = ResponseWrapper.created(request.id(), "SaleOrder");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseWrapper<Void> confirmSaleOrder(@PathVariable String id, @RequestParam String paymentId) {
        var command = ConfirmSaleOrderCommand.of(id, paymentId);
        salesOrderUseCase.confirmPayment(command);

        return ResponseWrapper.success("SaleOrder payment successfully confirmed.");
    }

    @PatchMapping("/{id}/fulfill")
    public ResponseWrapper<Void> fulfillOrder(@PathVariable String id) {
        var command = FullFillOrderCommand.of(id);

        salesOrderUseCase.fulfillOrder(command);
        return ResponseWrapper.success("SaleOrder successfully fulfilled.");
    }

    @PatchMapping("/{id}/deliver")
    public ResponseWrapper<Void> deliverOrder(@PathVariable String id) {
        var command = SendSaleOrderToDeliveryCommand.of(id);
        salesOrderUseCase.readyToDelivery(command);

        return ResponseWrapper.success("SaleOrder successfully marked as ready to delivery.");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseWrapper<Void> cancelOrder(@PathVariable String id,  @RequestParam(required = false) String reason) {
        var command = CancelSaleOrderCommand.of(id, reason);
        salesOrderUseCase.cancelOrder(command);

        return ResponseWrapper.success("SaleOrder successfully canceled.");
    }
}
