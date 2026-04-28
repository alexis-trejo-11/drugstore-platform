package microservice.order_service.external.users.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public class UserNotFoundByIDErr extends NotFoundException {
    public UserNotFoundByIDErr(UserID userID) {
            super("userID", "id", userID.value());
    }
}
