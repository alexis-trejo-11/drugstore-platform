package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Move product to afterwards", description = "Move a specific product emptyCart cart to afterwards list for a client.")
@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
		@ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
})
public @interface MoveCartItemsToAfterwards {
}
