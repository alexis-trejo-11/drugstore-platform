package microservice.cart_service.app.cart.adapter.input.web.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;


@Builder
public record CartItemResponse(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal discountPerUnit,
    BigDecimal subtotal,
    LocalDateTime addedAt,
    LocalDateTime updatedAt) {

}
