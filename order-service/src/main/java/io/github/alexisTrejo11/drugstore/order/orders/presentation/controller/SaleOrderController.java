package io.github.alexisTrejo11.drugstore.order.orders.presentation.controller;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.config.rate_limit.RateLimitType;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.DeleteOrderCommand;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.GetOrderByIDQuery;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.GetOrderDetailByIDQuery;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.SearchOrdersQuery;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.OrderApplicationFacade;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request.CreateOrderRequest;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request.OrderSearchRequest;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderDetailResponse;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
  @RateLimit(profile = RateLimitProfile.PUBLIC, type = RateLimitType.IP_BASED)
  @GetMapping("/search")
  public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
      @Valid OrderSearchRequest request) {

    SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
    Page<OrderQueryResult> resultPage = orderService.searchOrders(query);
    PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
    return ResponseWrapper.success(response, "Orders found successfully");
  }

  @GetOrderByIDOperation
  @RateLimit(profile = RateLimitProfile.STANDARD, type = RateLimitType.IP_BASED)
  @GetMapping("/{id}")
  public ResponseWrapper<OrderResponse> getOrderByID(@PathVariable String id) {
    var query = GetOrderByIDQuery.of(id);
    var queryResult = orderService.getOrderByID(query);
    var orderResponse = mapper.toResponse(queryResult);
    return ResponseWrapper.found(orderResponse, "PurchaseOrder");
  }

  @GetOrderDetailByIDOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @GetMapping("/{id}/detail")
  public ResponseWrapper<OrderDetailResponse> getOrderDetailByID(@PathVariable String id) {
    var query = GetOrderDetailByIDQuery.of(id);
    var queryResult = orderService.getOrderByID(query);
    var orderResponse = detailMapper.toDetail(queryResult);
    return ResponseWrapper.found(orderResponse, "PurchaseOrder Detail");
  }

  @CreateOrderOperation
  @CreateOrderRequestBody
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseWrapper<CreateOrderOperationSummary> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    if (request.deliveryMethod().requiresAddress()) {
      var command = request.toDeliveryOrderCommand();
      var result = orderService.createDeliveryOrder(command);
      return ResponseWrapper.created(result, "PurchaseOrder");
    }

    var command = request.toPickupOrderCommand();
    var result = orderService.createPickupOrder(command);
    return ResponseWrapper.created(result, "PurchaseOrder");
  }

  @DeleteOrderOperation
  @RateLimit(profile = RateLimitProfile.SENSITIVE, type = RateLimitType.IP_BASED)
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
