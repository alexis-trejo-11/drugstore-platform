package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Confirm purchaseOrder", description = "Confirms an order, transitioning it to CONFIRMED status and preparing it for processing.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order confirmed successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "ConfirmOrderSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Order Successfully Confirmed",
          "data": null,
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order not found"),
    @ApiResponse(responseCode = "400", description = "Order cannot be confirmed in current state"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface ConfirmOrderOperation {
}
