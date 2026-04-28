package microservice.order_service.orders.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

import microservice.order_service.orders.infrastructure.api.annotation.*;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.response.*;
import microservice.order_service.orders.application.queries.request.GetOrderByIDQuery;
import microservice.order_service.orders.application.queries.request.GetOrderDetailByIDQuery;
import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.dto.request.*;
import microservice.order_service.orders.infrastructure.api.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "Orders", description = "Endpoints for complete purchaseOrder lifecycle management: search, detail retrieval, creation, and logical/physical deletion.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/sale-orders", produces = "application/json")
public class SaleOrderController {

  private final OrderApplicationFacade orderService;
  private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
  private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;

  @SearchOrdersOperation
  @GetMapping("/search")
  public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
      @Valid OrderSearchRequest request) {

    SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
    Page<OrderQueryResult> resultPage = orderService.searchOrders(query);
    PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
    return ResponseWrapper.success(response, "Orders found successfully");
  }

  @GetOrderByIDOperation
  @GetMapping("/{id}")
  @RateLimit(maxRequests = 10, duration = 60, durationUnit = TimeUnit.SECONDS)
  public ResponseWrapper<OrderResponse> getOrderByID(@PathVariable String id) {
    var query = GetOrderByIDQuery.of(id);
    var queryResult = orderService.getOrderByID(query);
    var orderResponse = mapper.toResponse(queryResult);
    return ResponseWrapper.found(orderResponse, "PurchaseOrder");
  }

  @GetOrderDetailByIDOperation
  @GetMapping("/{id}/detail")
  public ResponseWrapper<OrderDetailResponse> getOrderDetailByID(@PathVariable String id) {
    var query = GetOrderDetailByIDQuery.of(id);
    var queryResult = orderService.getOrderByID(query);
    var orderResponse = detailMapper.toDetail(queryResult);
    return ResponseWrapper.found(orderResponse, "PurchaseOrder Detail");
  }

  @CreateOrderOperation
  @CreateOrderRequestBody
  @PostMapping(consumes = "application/json")
  public ResponseWrapper<CreateOrderCommandResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    if (request.deliveryMethod() != null) {
      var command = request.toDeliveryOrderCommand();
      var result = orderService.createDeliveryOrder(command);
      return ResponseWrapper.created(result, "PurchaseOrder");
    }

    var command = request.toPickupOrderCommand();
    var result = orderService.createPickupOrder(command);
    return ResponseWrapper.created(result, "PurchaseOrder");
  }

  @DeleteOrderOperation
  @DeleteMapping("/{id}")
  public ResponseWrapper<Void> deleteOrder(
      @PathVariable String id,
      @RequestParam boolean isHard) {
    var command = isHard ? DeleteOrderCommand.hardDelete(id)
        : DeleteOrderCommand.softDelete(id);

    orderService.deleteOrder(command);
    return ResponseWrapper.success("PurchaseOrder Successfully Deleted");
  }
}
