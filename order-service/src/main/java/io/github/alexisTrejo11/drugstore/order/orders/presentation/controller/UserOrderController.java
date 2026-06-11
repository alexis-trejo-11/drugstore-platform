package io.github.alexisTrejo11.drugstore.order.orders.presentation.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.config.rate_limit.RateLimitType;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.GetOrderByIDAndUserIDQuery;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderDetailResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.response.OrderQueryResult;
import io.github.alexisTrejo11.drugstore.order.orders.application.OrderApplicationFacade;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.annotation.GetUserOrderDetailOperation;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.annotation.GetUserOrdersOperation;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request.GetUserOrdersRequest;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderDetailResponse;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.response.OrderResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer Orders", description = "Endpoints for customers to access their own orders with pagination and detail views.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v2/customers/orders")
@RequiredArgsConstructor
public class UserOrderController {
  private final OrderApplicationFacade orderService;
  private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
  private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;

  @GetUserOrdersOperation
  @RateLimit(profile = RateLimitProfile.CUSTOMER_READ, type = RateLimitType.IP_BASED)
  @GetMapping("/{userID}")
  public ResponseWrapper<PageResponse<OrderResponse>> getUserOrders(
      @ModelAttribute GetUserOrdersRequest request,
      @PathVariable("userID") String customerId) {
    var query = request.toQuery(customerId);
    var resultPage = orderService.getOrdersByUserID(query);

    var ordersPaged = mapper.toResponsePage(resultPage);
    return ResponseWrapper.found(ordersPaged, "Orders");
  }

  @GetUserOrderDetailOperation
  @RateLimit(profile = RateLimitProfile.CUSTOMER_READ, type = RateLimitType.IP_BASED)
  @GetMapping("/{orderID}/{userID}")
  public ResponseWrapper<OrderDetailResponse> getUserOrderDetail(
      @PathVariable("orderID") String orderId,
      @PathVariable("userID") String userId) {

    var query = GetOrderByIDAndUserIDQuery.of(userId, orderId);
    var response = orderService.getOrderByIDAndUserID(query);

    var orderResponse = detailMapper.toDetail(response);
    return ResponseWrapper.found(orderResponse, "Order");
  }
}
