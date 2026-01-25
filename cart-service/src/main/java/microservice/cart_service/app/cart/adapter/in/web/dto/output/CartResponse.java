package microservice.cart_service.app.cart.adapter.in.web.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public record CartResponse(
    String cartId,
    String customerId,
    BigDecimal subtotal,
    LocalDateTime updatedAt,
    List<CartItemResponse> itemDetails) {
}
