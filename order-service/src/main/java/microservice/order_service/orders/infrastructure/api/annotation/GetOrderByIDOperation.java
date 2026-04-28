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
import microservice.order_service.orders.infrastructure.api.dto.response.OrderResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Get purchaseOrder summary by ID", description = "Retrieves a summarized representation of the purchaseOrder.\nIncludes basic status and cost fields.")
@Parameters({
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "PurchaseOrder UUID identifier", example = "c1d2e3f4-1111-2222-3333-abcdefabcdef")
})
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "PurchaseOrder found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class), examples = @ExampleObject(name = "GetOrderSuccess", value = """
        {
          "success": true,
          "code": 302,
          "message": "PurchaseOrder",
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
        """))),
    @ApiResponse(responseCode = "404", description = "PurchaseOrder not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "OrderNotFound", value = """
        {
          "success": false,
          "code": 404,
          "message": "PurchaseOrder not found"
        }
        """))),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface GetOrderByIDOperation {
}
