package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
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
@Operation(summary = "Start preparing purchaseOrder", description = "Marks order as PREPARING, indicating staff has begun fulfilling the order items.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order marked as preparing successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "StartPreparingSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Order Successfully Marked as Preparing",
          "data": null,
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order not found"),
    @ApiResponse(responseCode = "400", description = "Order cannot transition to PREPARING"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface StartPreparingOrderOperation {
}
