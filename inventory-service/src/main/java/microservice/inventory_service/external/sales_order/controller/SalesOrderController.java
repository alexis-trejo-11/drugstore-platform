package microservice.inventory_service.external.sales_order.controller;

import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.sales_order.service.SalesOrderUseCase;
import microservice.inventory_service.external.sales_order.controller.dto.CreateSalesOrderRequest;
import microservice.inventory_service.external.sales_order.controller.dto.SalesOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/orders/sales")
@RequiredArgsConstructor
public class SalesOrderController {
    private final SalesOrderUseCase salesOrderUseCase;

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createSalesOrder(@Valid @RequestBody CreateSalesOrderRequest request) {
        String orderId = salesOrderUseCase.createSalesOrder(request.toDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(orderId, "Sales PurchaseOrder"));
    }

    @GetMapping("/{id}")
    public ResponseWrapper<SalesOrderResponse> getSalesOrderById(@PathVariable String id) {
        var order = salesOrderUseCase.getSalesOrderById(id);
        var response = SalesOrderResponse.fromDTO(order);
        return ResponseWrapper.found(response, "Sales PurchaseOrder");
    }

    @PatchMapping("/{id}/pay")
    public ResponseWrapper<Void> confirmPayment(@PathVariable String id, @RequestParam String paymentId) {
        salesOrderUseCase.confirmPayment(id, paymentId);
        return ResponseWrapper.updated(null, "Payment confirmed");
    }

    @PatchMapping("/{id}/fulfill")
    public ResponseWrapper<Void> fulfillOrder(@PathVariable String id) {
        salesOrderUseCase.fulfillOrder(id);
        return ResponseWrapper.updated(null, "PurchaseOrder fulfilled");
    }

    @PatchMapping("/{id}/deliver")
    public ResponseWrapper<Void> deliverOrder(@PathVariable String id) {
        salesOrderUseCase.readyToDelivery(id);
        return ResponseWrapper.updated(null, "PurchaseOrder delivered");
    }

    @DeleteMapping("/{id}")
    public ResponseWrapper<Void> cancelOrder(@PathVariable String id,  @RequestParam(required = false) String reason) {
        salesOrderUseCase.cancelOrder(id, reason);
        return ResponseWrapper.deleted(null, "Sales PurchaseOrder");
    }
}
