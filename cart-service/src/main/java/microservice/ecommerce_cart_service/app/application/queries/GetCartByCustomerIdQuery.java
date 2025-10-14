package microservice.ecommerce_cart_service.app.application.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;

public class GetCartByCustomerIdQuery {
    private CustomerId customerId;
    
    public GetCartByCustomerIdQuery(CustomerId customerId) {
        this.customerId = customerId;
    }
}
