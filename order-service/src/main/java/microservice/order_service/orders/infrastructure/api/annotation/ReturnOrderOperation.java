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
@Operation(summary = "Return purchaseOrder", description = "Marks order as RETURNED due to failed delivery or customer request with a provided reason.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order marked as returned successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "ReturnOrderSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "Order Successfully Marked as Returned",
          "data": null,
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Order not found"),
    @ApiResponse(responseCode = "400", description = "Order cannot be returned in current state"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface ReturnOrderOperation {
}
