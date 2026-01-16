package microservice.product_service.app.infrastructure.in.web.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Search Products", description = "Retrieves a paginated list of products based on provided search criteria")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product found",
          "data": [
            {
              "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
              "name": "Paracetamol 500mg",
              "description": "Tablets for pain and fever relief.",
              "category": "ANALGESICS",
              "price": 8.75,
              "stockQuantity": 2000
            }
          ]
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid query parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Bad Request Example", value = """
        {
          "success": false,
          "message": "Invalid Argument Type",
          "data": "Parameter 'page' has an invalid type. Expected 'int'."
        }
        """))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Internal Server Error", value = """
        {
          "success": false,
          "message": "Internal Server Error",
          "data": "An unexpected error occurred. Please try again later."
        }
        """)))
})
public @interface SearchProductsOperation {
}
