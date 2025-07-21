package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(ProductId productId, String name, BigDecimal price, boolean isAvailable) {
}
