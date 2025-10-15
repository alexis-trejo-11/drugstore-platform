package microservice.order_service.orders.application.exceptions;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.external.address.domain.model.AddressID;

public class UserAddressNotFound extends NotFoundException {
    public UserAddressNotFound(AddressID addressID) {
        super("User Delivery Address", "id", addressID.value());
    }
}

