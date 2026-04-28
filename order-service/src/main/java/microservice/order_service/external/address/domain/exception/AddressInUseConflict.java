package microservice.order_service.external.address.domain.exception;

import libs_kernel.exceptions.ConflictException;
import microservice.order_service.external.address.domain.model.AddressID;

public class AddressInUseConflict extends ConflictException {
    public AddressInUseConflict(AddressID addressID) {
        super(String.format("Address with ID %s is in use by ongoing orders and cannot be deleted or modified.", addressID.value()), "address_in_use_conflict");
    }
}
