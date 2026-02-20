package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "GetCart", description = "Retrieves the shopping cart associated with a specific identifier. Can include items and afterwards items based on request parameters.")
public @interface GetCartOperation {
}
