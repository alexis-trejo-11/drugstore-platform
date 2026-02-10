package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Create a new purchaseOrder", description = "Registers a new purchaseOrder with its items and shipping data. Validates content and returns the created identifier.")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "PurchaseOrder created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateOrderCommandResponse.class), examples = @ExampleObject(name = "CreateOrderSuccess", value = """
        {
          "success": true,
          "code": 201,
          "message": "PurchaseOrder",
          "data": {
            "purchaseOrderId": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
            "status": "PENDING"
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid payload"),
    @ApiResponse(responseCode = "409", description = "Conflict (duplicate or other rule)"),
    @ApiResponse(responseCode = "422", description = "Semantic validation error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface CreateOrderOperation {
}
