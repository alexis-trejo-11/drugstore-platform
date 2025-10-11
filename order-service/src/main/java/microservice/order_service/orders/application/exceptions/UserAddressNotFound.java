package microservice.order_service.orders.application.exceptions;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public class UserAddressNotFound extends NotFoundException {
    public UserAddressNotFound(AddressID addressID) {
        super("User Delivery Address", "id", addressID.value());
    }
}

