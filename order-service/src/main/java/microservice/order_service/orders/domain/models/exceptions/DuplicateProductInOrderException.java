package microservice.order_service.orders.domain.models.exceptions;

import lombok.extern.slf4j.Slf4j;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;

import java.util.HashMap;
import java.util.Map;


public class DuplicateProductInOrderException extends OrderDomainException {
    private final String productId;

    public DuplicateProductInOrderException(ProductID productID) {
        super(
                String.format("Order already contains product with ID: %s", productID.value()),
                "DUPLICATE_PRODUCT_IN_ORDER"
        );
        this.productId = productID.value();
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public Map<String, Object> getLoggingContext() {
        Map<String, Object> context = new HashMap<>(super.getLoggingContext());
        context.put("productId", productId);
        return context;
    }
}

