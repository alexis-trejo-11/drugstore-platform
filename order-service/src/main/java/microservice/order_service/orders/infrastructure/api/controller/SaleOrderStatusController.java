package microservice.order_service.orders.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.annotation.*;
import microservice.order_service.orders.infrastructure.api.dto.request.ConfirmOrderRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order Status", description = "Endpoints for managing order status transitions and workflow state changes.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sale-orders")
public class SaleOrderStatusController {
  private final OrderApplicationFacade orderService;

  @ConfirmOrderOperation
  @PatchMapping("/{id}/confirm")
  private ResponseWrapper<Void> confirmOrder(@PathVariable String id, @Valid @RequestBody ConfirmOrderRequest request) {
    var command = request.toCommand(id);
    orderService.confirmOrder(command);
    return ResponseWrapper.success("Order Successfully Confirmed");
  }

  @StartPreparingOrderOperation
  @PatchMapping("/{id}/start-preparing")
  private ResponseWrapper<UpdateOrderStatusCommandResult> startPreparingOrder(@PathVariable String id) {
    var command = PrepareOrderCommand.of(id);
    orderService.startPreparingOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Preparing");
  }

  @ShipOrderOperation
  @PatchMapping("/{id}/ship/track_number/{trackNumber}")
  private ResponseWrapper<Void> shipOrder(@PathVariable String id, @PathVariable String trackNumber) {
    var command = ShipOrderCommand.of(id, trackNumber);
    orderService.shipOrder(command);
    return ResponseWrapper.success("Order Successfully Shipped");
  }

  @ReturnOrderOperation
  @PatchMapping("/{id}/return")
  private ResponseWrapper<Void> returnOrder(@PathVariable String id, @RequestParam String reason) {
    var command = OrderDeliverFailCommand.of(id, reason);
    orderService.returnOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Returned");
  }

  @ReadyForPickupOperation
  @PatchMapping("/{id}/ready-pickup")
  private ResponseWrapper<Void> setOrderAsReadyToPickup(@PathVariable String id) {
    var command = OrderReadyToPickupCommand.of(id);
    orderService.readyForPickupOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Ready for Pickup");
  }

  @CompleteOrderOperation
  @PatchMapping("/{id}/complete")
  private ResponseWrapper<Void> completeOrder(@PathVariable String id) {
    var command = CompleteOrderCommand.of(id);
    orderService.completeOrder(command);
    return ResponseWrapper.success("Order Successfully Completed");
  }

  @CancelOrderOperation
  @PutMapping("/{id}/cancel")
  private ResponseWrapper<CancelOrderCommandResponse> cancelOrder(@PathVariable String id,
      @RequestParam String reason) {
    var command = CancelOrderCommand.adminCancel(id, reason);
    var queryResult = orderService.cancelOrder(command);
    return ResponseWrapper.success(queryResult, "Order Successfully Canceled");
  }
}
