package microservice.cart_service.app.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get Customer Cart", description = "Retrieves the shopping cart associated with a specific customer by their ID")
public @interface GetCustomerCartOperation {
}
