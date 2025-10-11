package microservice.order_service.external.users.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;

public class UserNotFoundByPhoneErr extends NotFoundException {
    public UserNotFoundByPhoneErr(String emailValue) {
        super("userID", "email", emailValue);
    }
}
