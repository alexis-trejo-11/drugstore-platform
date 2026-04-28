package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Update My Cart", description = "Update the shopping cart items for the authenticated customer. This operation does not include afterwards items.")
public @interface UpdateMyCartItemsOperation {
}
