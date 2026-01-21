package microservice.product_service.app.infrastructure.in.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Restore Product", description = "Restores a previously soft-deleted product by its unique UUID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product successfully restored", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product successfully restored",
          "data": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Not Found Example", value = """
        {
          "success": false,
          "message": "Product Not Found",
          "data": "Product with ID 'non-existent-uuid' not found."
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid UUID format or product not deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Bad Request Example", value = """
        {
          "success": false,
          "message": "Invalid Argument Type",
          "data": "Parameter 'productId' has an invalid type. Expected 'UUID'."
        }
        """)))
})
public @interface RestoreProductOperation {
}
