package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public class AddressNotFoundByOrderIDErr extends NotFoundException {
    public AddressNotFoundByOrderIDErr(OrderID userID) {
        super("Delivery Address", "orderID", userID.value());
    }
}

