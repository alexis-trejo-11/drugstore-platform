package microservice.users.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends DrugstoreException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "PH-403");
    }
}
