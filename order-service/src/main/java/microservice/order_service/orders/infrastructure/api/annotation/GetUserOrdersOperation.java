package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import libs_kernel.page.PageResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Get customer's purchaseOrders", description = "Retrieves paginated list of orders for a specific customer with filtering and sorting options.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Customer orders retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class), examples = @ExampleObject(name = "GetUserOrdersSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Orders found successfully",
          "data": {
            "content": [
              {
                "id": "order-123",
                "status": "COMPLETED",
                "deliveryMethod": "HOME_DELIVERY",
                "shippingCost": 100.00,
                "createdAt": "2025-01-10T10:00:00"
              }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 5,
            "totalPages": 1,
            "hasNext": false
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid customer ID or pagination parameters"),
    @ApiResponse(responseCode = "404", description = "Customer not found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface GetUserOrdersOperation {
}
