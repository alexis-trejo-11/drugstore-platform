package microservice.ecommerce_cart_service.app.application.command.cart;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartCommand {
    private CustomerId customerId;
}

