package microservice.order_service.orders.domain.models.exceptions;

import microservice.order_service.orders.domain.models.valueobjects.ProductID;

public class DuplicateProductInOrderException extends OrderDomainException {
    public DuplicateProductInOrderException(ProductID productID) {
        super(String.format("Order already contains product with ID: %s", productID.value()), "DUPLICATE_PRODUCT_IN_ORDER");
    }
}
