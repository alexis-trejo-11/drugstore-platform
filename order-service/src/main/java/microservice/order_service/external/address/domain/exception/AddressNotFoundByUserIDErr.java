package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public class AddressNotFoundByUserIDErr extends NotFoundException {
    public AddressNotFoundByUserIDErr(UserID userID) {
        super("Delivery Address", userID.value());
    }
}
