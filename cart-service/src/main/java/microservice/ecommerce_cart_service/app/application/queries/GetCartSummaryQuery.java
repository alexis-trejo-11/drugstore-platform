package microservice.ecommerce_cart_service.app.application.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCartSummaryQuery {
    private Long cartId;
}
