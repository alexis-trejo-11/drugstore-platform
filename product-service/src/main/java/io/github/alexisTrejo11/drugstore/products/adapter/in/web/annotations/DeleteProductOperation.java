package io.github.alexisTrejo11.drugstore.products.adapter.in.web.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Soft Delete Product", description = "Marks a product as inactive or deleted without permanently removing it")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product successfully soft-deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product deleted",
          "data": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class)))
})
public @interface DeleteProductOperation {
}
