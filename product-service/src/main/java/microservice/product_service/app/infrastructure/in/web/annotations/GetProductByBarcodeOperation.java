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
@Operation(summary = "Get Product by Barcode", description = "Retrieves a single product's details using its barcode number")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product found",
          "data": {
            "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
            "name": "Paracetamol 500mg",
            "barcode": "9876543210987",
            "description": "Tablets for pain and fever relief.",
            "category": "ANALGESICS",
            "price": 8.75,
            "stockQuantity": 2000
          }
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Not Found Example", value = """
        {
          "success": false,
          "message": "Product Not Found",
          "data": "Product with barcode '1234567890123' not found."
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid barcode format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Bad Request Barcode", value = """
        {
          "success": false,
          "message": "Invalid Argument Type",
          "data": "Parameter 'barcode' has an invalid format."
        }
        """)))
})
public @interface GetProductByBarcodeOperation {
}
