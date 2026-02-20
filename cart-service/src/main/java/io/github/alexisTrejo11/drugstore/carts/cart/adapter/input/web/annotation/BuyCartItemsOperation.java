package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Buy Cart Items", description = "Process the purchase of items currently in the authenticated customer's shopping cart."+
" This operation finalizes the transaction for the items present in the cart at the time of the request.")
public @interface BuyCartItemsOperation {
}
