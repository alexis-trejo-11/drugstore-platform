package microservice.cart_service.app.cart.adapter.input.web.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Cart Admin Management", description = "Operations related to administrative cart management, including retrieval and oversight of customer carts")
public @interface CartAdminControllerTag {
}
