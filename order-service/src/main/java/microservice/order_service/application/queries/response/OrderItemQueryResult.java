package microservice.order_service.application.queries.response;

import lombok.Builder;
import lombok.Data;
import microservice.order_service.domain.models.valueobjects.ProductID;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemQueryResult {
    private ProductID productID;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private Boolean prescriptionRequired;
}

