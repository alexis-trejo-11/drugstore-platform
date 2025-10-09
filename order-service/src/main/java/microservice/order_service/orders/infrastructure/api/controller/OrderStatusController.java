package microservice.order_service.orders.infrastructure.api.controller;

import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.dto.request.ConfirmOrderRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders/status")
public class OrderStatusController {
    private final OrderApplicationFacade orderService;

    @PatchMapping("/{id}/confirm")
    private ResponseWrapper<Void> confirmOrder(@PathVariable String id, @Valid @RequestBody ConfirmOrderRequest request) {
        var command = request.toCommand(id);
        orderService.confirmOrder(command);
        return ResponseWrapper.success("Order Successfully Confirmed");
    }

    @PatchMapping("/{id}/start-preparing")
    private ResponseWrapper<Void> startPreparingOrder(@PathVariable String id) {
        var command = PrepareOrderCommand.of(id);
        orderService.startPreparingOrder(command);
        return ResponseWrapper.success("Order Successfully Marked as Preparing");
    }

    @PatchMapping("/{id}/ship/track_number/{trackNumber}")
    private ResponseWrapper<Void> shipOrder(@PathVariable String id, @PathVariable String trackNumber) {
        var command = ShipOrderCommand.of(id, trackNumber);
        orderService.shipOrder(command);
        return ResponseWrapper.success("Order Successfully Shipped");
    }

    @PatchMapping("/{id}/return")
    private ResponseWrapper<Void> returnOrder(@PathVariable String id, @RequestParam String reason) {
        var command = OrderDeliverFailCommand.of(id, reason);
        orderService.returnOrder(command);
        return ResponseWrapper.success("Order Successfully Marked as Returned");
    }

    @PatchMapping("/{id}/ready-pickup")
    private ResponseWrapper<Void> setOrderAsReadyToPickup(@PathVariable String id) {
        var command = OrderReadyToPickupCommand.of(id);
        orderService.readyForPickupOrder(command);
        return ResponseWrapper.success("Order Successfully Marked as Ready for Pickup");
    }

    @PatchMapping("/{id}/complete")
    private ResponseWrapper<Void> completeOrder(@PathVariable String id) {
        var command = CompleteOrderCommand.of(id);
        orderService.completeOrder(command);
        return ResponseWrapper.success("Order Successfully Completed");
    }
    @PutMapping("/{id}/cancel")
    private ResponseWrapper<CancelOrderCommandResponse> cancelOrder(@PathVariable String id, @RequestParam String reason) {
        var command = CancelOrderCommand.adminCancel(id, reason);
        var queryResult = orderService.cancelOrder(command);
        return ResponseWrapper.success(queryResult, "Order Successfully Canceled");
    }
}
