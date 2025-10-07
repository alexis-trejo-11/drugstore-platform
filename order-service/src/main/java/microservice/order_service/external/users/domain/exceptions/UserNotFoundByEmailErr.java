package microservice.order_service.external.users.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;

public class UserNotFoundByEmailErr extends NotFoundException {
    public UserNotFoundByEmailErr(String emailValue) {
        super("user", "email", emailValue);
    }
}
