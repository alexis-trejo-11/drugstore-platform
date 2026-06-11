package io.github.alexisTrejo11.drugstore.order.orders.presentation.controller;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.status.*;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.config.rate_limit.RateLimitType;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CancelOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.UpdateOrderStatusOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.application.OrderApplicationFacade;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request.ConfirmOrderRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order Status", description = "Endpoints for managing order status transitions and workflow state changes.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sale-orders")
public class SaleOrderStatusController {
  private final OrderApplicationFacade orderService;

  @ConfirmOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/confirm")
  public ResponseWrapper<Void> confirmOrder(@PathVariable String id, @Valid @RequestBody ConfirmOrderRequest request) {
    var command = request.toCommand(id);
    orderService.confirmOrder(command);
    return ResponseWrapper.success("Order Successfully Confirmed");
  }

  @StartPreparingOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/start-preparing")
  public ResponseWrapper<UpdateOrderStatusOperationSummary> startPreparingOrder(@PathVariable String id) {
    var command = PrepareOrderCommand.of(id);
    orderService.startPreparingOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Preparing");
  }

  @ShipOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/ship/track_number/{trackNumber}")
  public ResponseWrapper<Void> shipOrder(@PathVariable String id, @PathVariable String trackNumber) {
    var command = ShipOrderCommand.of(id, trackNumber);
    orderService.shipOrder(command);
    return ResponseWrapper.success("Order Successfully Shipped");
  }

  @ReturnOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/return")
  public ResponseWrapper<Void> returnOrder(@PathVariable String id, @RequestParam String reason) {
    var command = OrderDeliverFailCommand.of(id, reason);
    orderService.returnOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Returned");
  }

  @ReadyForPickupOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/ready-pickup")
  public ResponseWrapper<Void> setOrderAsReadyToPickup(@PathVariable String id) {
    var command = OrderReadyToPickupCommand.of(id);
    orderService.readyForPickupOrder(command);
    return ResponseWrapper.success("Order Successfully Marked as Ready for Pickup");
  }

  @CompleteOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PatchMapping("/{id}/complete")
  public ResponseWrapper<Void> completeOrder(@PathVariable String id) {
    var command = CompleteOrderCommand.of(id);
    orderService.completeOrder(command);
    return ResponseWrapper.success("Order Successfully Completed");
  }

  @CancelOrderOperation
  @RateLimit(profile = RateLimitProfile.ADMIN, type = RateLimitType.IP_BASED)
  @PutMapping("/{id}/cancel")
  public ResponseWrapper<CancelOrderOperationSummary> cancelOrder(@PathVariable String id,
      @RequestParam String reason) {
    var command = CancelOrderCommand.adminCancel(id, reason);
    var queryResult = orderService.cancelOrder(command);
    return ResponseWrapper.success(queryResult, "Order Successfully Canceled");
  }
}
