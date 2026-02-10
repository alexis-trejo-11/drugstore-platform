package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Cancel purchaseOrder", description = "Cancels an order with provided reason. Admin operation that requires authorization.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order canceled successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "CancelOrderSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Order Successfully Canceled",
          "data": {
            "cancellationReason": "Requested by admin"
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order not found"),
    @ApiResponse(responseCode = "400", description = "Order cannot be canceled in current state"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface CancelOrderOperation {
}
