package io.github.alexisTrejo11.drugstore.products.adapter.in.web.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.github.alexisTrejo11.drugstore.products.adapter.in.web.dto.UpdateProductRequest;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Update Product", description = "Updates an existing product's details using its unique UUID. Supports partial updates")
@RequestBody(description = "Product details to update. Fields omitted or null will not be changed.", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateProductRequest.class), examples = @ExampleObject(name = "Example Update Request (Partial)", value = """
    {
      "name": "Updated Paracetamol",
      "price": 9.50,
      "stockQuantity": 1800
    }
    """)))
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product updated",
          "data": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class)))
})
public @interface UpdateProductOperation {
}
