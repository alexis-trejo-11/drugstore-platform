package microservice.order_service.external.users.domain.exceptions;

import libs_kernel.exceptions.ConflictException;

public class UserCredentialsConflict extends ConflictException {
    public UserCredentialsConflict(String field, String value) {
        super(String.format("The %s '%s' is already in use.", field, value), "USER_CREDENTIALS_CONFLICT");
    }
}
