package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public class AddressNotFoundForUserErr extends NotFoundException {
    public AddressNotFoundForUserErr(UserID userID, AddressID addressID) {
        super("Delivery Address", " with id " + addressID.value() + " for userID " + userID.value(), "id");
    }
}
