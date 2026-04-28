package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.ConflictException;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public class OrderWithNoAddressConflict extends ConflictException {
    public OrderWithNoAddressConflict(OrderID orderID) {
        super("Order with ID " + orderID.value() + " has no associated address.", "ORDER_NO_ADDRESS");
    }
}
