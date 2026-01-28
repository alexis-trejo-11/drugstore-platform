package microservice.cart_service.app.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Restore Items From Afterwards", description = "Restore items from the afterwards list back to the shopping cart for the authenticated customer.")
public @interface RestoreItemsFromAfterwardsOperation {
}
