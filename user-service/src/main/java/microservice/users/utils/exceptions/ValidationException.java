package microservice.users.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends DrugstoreException {
    public ValidationException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
