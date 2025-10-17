package microservice.order_service.orders.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.RateLimit;
import libs_kernel.mapper.EntityDetailMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.queries.request.GetOrderByIDQuery;
import microservice.order_service.orders.application.queries.request.GetOrderDetailByIDQuery;
import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.application.queries.response.OrderDetailResult;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.ports.input.OrderApplicationFacade;
import microservice.order_service.orders.infrastructure.api.dto.request.CreateOrderRequest;
import microservice.order_service.orders.infrastructure.api.dto.request.OrderSearchRequest;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(
        name = "Orders",
        description = "Endpoints for complete order lifecycle management: search, detail retrieval, creation, and logical/physical deletion."
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/orders", produces = "application/json")
public class OrderController {

    private final OrderApplicationFacade orderService;
    private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
    private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;

    @Operation(
            summary = "Search orders (paginated and filtered)",
            description = "Performs a paginated search of orders applying dynamic filters.\n"
                    + "Typical parameters supported in OrderSearchRequest: status, deliveryMethod, dateFrom, dateTo, userId, page, size, sort.\n"
                    + "Returns a standard ResponseWrapper container with pagination metadata.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "SearchOrdersSuccess",
                                    summary = "Example of orders page",
                                    value = """
                                            {
                                              "success": true,
                                              "code": 200,
                                              "message": "Orders found successfully",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
                                                    "status": "PENDING",
                                                    "deliveryMethod": "HOME_DELIVERY",
                                                    "shippingCost": 120.50,
                                                    "taxAmount": 19.28,
                                                    "createdAt": "2025-01-15T10:15:30",
                                                    "updatedAt": "2025-01-15T11:00:00"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 20,
                                                "totalElements": 1,
                                                "totalPages": 1,
                                                "hasNext": false,
                                                "hasPrevious": false,
                                                "sort": "createdAt,DESC"
                                              },
                                              "errors": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SearchOrdersBadRequest",
                                    value = """
                                            {
                                              "success": false,
                                              "code": 400,
                                              "message": "Invalid search criteria",
                                              "data": null,
                                              "errors": {
                                                "status": "Unsupported status value"
                                              }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
            @Parameter(description = "Search filter object (in query string). E.g.: /search?status=PENDING&userId=...&page=0&size=20")
            @Valid OrderSearchRequest request) {

        SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
        Page<OrderQueryResult> resultPage = orderService.searchOrders(query);
        PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
        return ResponseWrapper.success(response, "Orders found successfully");
    }

    @Operation(
            summary = "Get order summary by ID",
            description = "Retrieves a summarized representation of the order.\nIncludes basic status and cost fields.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "Order UUID identifier",
                    example = "c1d2e3f4-1111-2222-3333-abcdefabcdef")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    name = "GetOrderSuccess",
                                    value = """
                                            {
                                              "success": true,
                                              "code": 302,
                                              "message": "Order",
                                              "data": {
                                                "id": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
                                                "status": "CONFIRMED",
                                                "deliveryMethod": "STORE_PICKUP",
                                                "shippingCost": 0.00,
                                                "taxAmount": 10.00,
                                                "createdAt": "2025-01-10T09:30:00",
                                                "updatedAt": "2025-01-10T10:00:00"
                                              },
                                              "errors": null
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "OrderNotFound",
                                    value = """
                                            {
                                              "success": false,
                                              "code": 404,
                                              "message": "Order not found",
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @RateLimit(maxRequests = 10, duration = 60, durationUnit = TimeUnit.SECONDS)
    public ResponseWrapper<OrderResponse> getOrderByID(@NotNull @PathVariable String id) {
        var query = GetOrderByIDQuery.of(id);
        var queryResult = orderService.getOrderByID(query);
        var orderResponse = mapper.toResponse(queryResult);
        return ResponseWrapper.found(orderResponse, "Order");
    }

    @Operation(
            summary = "Get full order detail",
            description = "Returns extended information: items, address, tracking, detailed costs, and timestamps.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "Order UUID identifier",
                    example = "c1d2e3f4-1111-2222-3333-abcdefabcdef")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order detail found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "OrderDetailSuccess",
                                    value = """
                                            {
                                              "success": true,
                                              "code": 302,
                                              "message": "Order Detail",
                                              "data": {
                                                "id": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
                                                "status": "SHIPPED",
                                                "deliveryMethod": "HOME_DELIVERY",
                                                "shippingCost": 150.00,
                                                "taxAmount": 24.00,
                                                "paymentId": "pay_987654321",
                                                "deliveryTrackingNumber": "TRK123456789",
                                                "items": [
                                                  {
                                                    "productId": "prod-123",
                                                    "productName": "Vitamin C",
                                                    "unitPrice": 99.90,
                                                    "quantity": 2,
                                                    "currency": "MXN",
                                                    "isPrescriptionRequired": false
                                                  }
                                                ],
                                                "address": {
                                                  "id": "addr-123",
                                                  "country": "Mexico",
                                                  "state": "CDMX",
                                                  "city": "Mexico City",
                                                  "street": "Av Reforma 100",
                                                  "zipCode": "01000"
                                                },
                                                "createdAt": "2025-01-09T08:00:00",
                                                "updatedAt": "2025-01-10T12:00:00"
                                              },
                                              "errors": null
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Detail not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}/detail")
    public ResponseWrapper<OrderDetailResponse> getOrderDetailByID(@NotNull @PathVariable String id) {
        var query = GetOrderDetailByIDQuery.of(id);
        var queryResult = orderService.getOrderByID(query);
        var orderResponse = detailMapper.toDetail(queryResult);
        return ResponseWrapper.found(orderResponse, "Order Detail");
    }

    @Operation(
            summary = "Create a new order",
            description = "Registers a new order with its items and shipping data. Validates content and returns the created identifier.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @RequestBody(
            required = true,
            description = "Order creation payload",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateOrderRequest.class),
                    examples = @ExampleObject(
                            name = "CreateOrderRequest",
                            value = """
                                    {
                                      "userId": "b2a11111-2222-3333-4444-555566667777",
                                      "deliveryMethod": "HOME_DELIVERY",
                                      "addressId": "addr-123",
                                      "notes": "Leave at front desk",
                                      "items": [
                                        {
                                          "productId": "prod-123",
                                          "productName": "Vitamin C",
                                          "unitPrice": 99.90,
                                          "quantity": 2,
                                          "currency": "MXN",
                                          "isPrescriptionRequired": false
                                        }
                                      ]
                                    }
                                    """
                    ))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderCommandResponse.class),
                            examples = @ExampleObject(
                                    name = "CreateOrderSuccess",
                                    value = """
                                            {
                                              "success": true,
                                              "code": 201,
                                              "message": "Order",
                                              "data": {
                                                "orderId": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
                                                "status": "PENDING"
                                              },
                                              "errors": null
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate or other rule)"),
            @ApiResponse(responseCode = "422", description = "Semantic validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = "application/json")
    public ResponseWrapper<CreateOrderCommandResponse> createOrder(
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateOrderRequest request) {
        if (request.deliveryMethod() != null) {
            var command = request.toDeliveryOrderCommand();
            var result = orderService.createDeliveryOrder(command);
            return ResponseWrapper.created(result, "Order");
        }

        var command = request.toPickupOrderCommand();
        var result = orderService.createPickupOrder(command);
        return ResponseWrapper.created(result, "Order");
    }

    @Operation(
            summary = "Delete an order",
            description = "Deletes an order by ID. Supports logical (soft delete) by default or physical using isHard=true.\nSoft delete usually sets deletedAt; hard delete removes the record.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "Order UUID identifier to delete",
                    example = "c1d2e3f4-1111-2222-3333-abcdefabcdef"),
            @Parameter(name = "isHard", in = ParameterIn.QUERY, required = false,
                    description = "true for physical deletion; false (default) for logical deletion",
                    example = "false")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logical/physical deletion successful",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "DeleteOrderSuccess",
                                    value = """
                                            {
                                              "success": true,
                                              "code": 200,
                                              "message": "Order Successfully Deleted",
                                              "data": null,
                                              "errors": null
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseWrapper<Void> deleteOrder(
            @PathVariable String id,
            @RequestParam boolean isHard) {
        var command = isHard ? DeleteOrderCommand.hardDelete(id)
                : DeleteOrderCommand.softDelete(id);

        orderService.deleteOrder(command);
        return ResponseWrapper.success("Order Successfully Deleted");
    }
}
