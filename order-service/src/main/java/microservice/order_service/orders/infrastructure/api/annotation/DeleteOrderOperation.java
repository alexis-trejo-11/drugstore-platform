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
@Operation(summary = "Delete an purchaseOrder", description = "Deletes an purchaseOrder by ID. Supports logical (soft delete) by default or physical using isHard=true.\nSoft delete usually sets deletedAt; hard delete removes the record.")
@Parameters({
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "PurchaseOrder UUID identifier to delete", example = "c1d2e3f4-1111-2222-3333-abcdefabcdef"),
    @Parameter(name = "isHard", in = ParameterIn.QUERY, required = false, description = "true for physical deletion; false (default) for logical deletion", example = "false")
})
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Logical/physical deletion successful", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "DeleteOrderSuccess", value = """
        {
          "success": true,
          "code": 200,
          "message": "PurchaseOrder Successfully Deleted",
          "data": null,
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "PurchaseOrder not found"),
    @ApiResponse(responseCode = "400", description = "Invalid ID"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface DeleteOrderOperation {
}
