package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.external.address.domain.model.AddressID;

public class AddressNotFoundByIDErr extends NotFoundException {
    public AddressNotFoundByIDErr(AddressID addressID) {
        super("Delivery Address", "id", addressID.value());
    }
}
