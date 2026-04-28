package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@Operation(summary = "Ship purchaseOrder", description = "Marks order as SHIPPED with tracking number for delivery method orders.")
@Parameters({
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Order ID"),
    @Parameter(name = "trackNumber", in = ParameterIn.PATH, required = true, description = "Shipping tracking number")
})
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order shipped successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "ShipOrderSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Order Successfully Shipped",
          "data": null,
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order not found"),
    @ApiResponse(responseCode = "400", description = "Order cannot be shipped in current state"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface ShipOrderOperation {
}
