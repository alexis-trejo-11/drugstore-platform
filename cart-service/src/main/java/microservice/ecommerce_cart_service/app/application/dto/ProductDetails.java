package microservice.ecommerce_cart_service.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {
    private String productId;
    private String productName;
    private BigDecimal productPrice = BigDecimal.ZERO;
}
