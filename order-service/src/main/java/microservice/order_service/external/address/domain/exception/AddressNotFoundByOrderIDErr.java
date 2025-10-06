package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public class AddressNotFoundByOrderIDErr extends NotFoundException {
    public AddressNotFoundByOrderIDErr(OrderID userID) {
        super("Delivery Address", userID.value());
    }
}

