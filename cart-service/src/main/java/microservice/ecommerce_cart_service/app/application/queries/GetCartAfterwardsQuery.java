package microservice.ecommerce_cart_service.app.application.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCartAfterwardsQuery {
    private CartId cartId;
    private QueryPageData pageData;
}
