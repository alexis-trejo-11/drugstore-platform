package microservice.order_service.orders.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

import microservice.order_service.orders.application.queries.request.GetOrderByIDAndUserIDQuery;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.annotation.*;
import microservice.order_service.orders.infrastructure.api.dto.request.GetUserOrdersRequest;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderResponse;
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
  @GetMapping("/{userID}")
  public ResponseWrapper<PageResponse<OrderResponse>> getUserOrders(
      @ModelAttribute GetUserOrdersRequest request,
      @PathVariable String customerId) {
    var query = request.toQuery(customerId);
    var resultPage = orderService.getOrdersByUserID(query);

    var ordersPaged = mapper.toResponsePage(resultPage);
    return ResponseWrapper.found(ordersPaged, "Orders");
  }

  @GetUserOrderDetailOperation
  @GetMapping("/{orderID}/{userID}")
  public ResponseWrapper<OrderDetailResponse> getUserOrderDetail(
      @PathVariable String customerId,
      @PathVariable String orderId) {

    var query = GetOrderByIDAndUserIDQuery.of(customerId, orderId);
    var response = orderService.getOrderByIDAndUserID(query);

    var orderResponse = detailMapper.toDetail(response);
    return ResponseWrapper.found(orderResponse, "Order");
  }
}
