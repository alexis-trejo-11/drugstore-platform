package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Get customer's purchaseOrder detail", description = "Retrieves detailed information for a specific customer's order including items, address, and tracking.")
@Parameters({
    @Parameter(name = "customerId", in = ParameterIn.PATH, required = true, description = "Customer UUID identifier"),
    @Parameter(name = "orderId", in = ParameterIn.PATH, required = true, description = "Order UUID identifier")
})
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order detail retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDetailResponse.class), examples = @ExampleObject(name = "GetUserOrderDetailSuccess", value = """
        {
          "success": true,
          "code": 302,
          "message": "Order",
          "data": {
            "id": "order-123",
            "status": "SHIPPED",
            "deliveryMethod": "HOME_DELIVERY",
            "shippingCost": 100.00,
            "taxAmount": 16.00,
            "items": [
              {
                "productId": "prod-456",
                "productName": "Aspirin",
                "quantity": 1,
                "unitPrice": 50.00
              }
            ],
            "address": {
              "street": "123 Main St",
              "city": "Mexico City"
            },
            "deliveryTrackingNumber": "TRK789456"
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order or customer not found"),
    @ApiResponse(responseCode = "403", description = "Forbidden - Order does not belong to customer"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface GetUserOrderDetailOperation {
}
