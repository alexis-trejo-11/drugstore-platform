package microservice.ecommerce_cart_service.app.application.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCartByIdQuery {
    private CartId cartId;
    private GetCartItemsQuery itemsQuery;
}
