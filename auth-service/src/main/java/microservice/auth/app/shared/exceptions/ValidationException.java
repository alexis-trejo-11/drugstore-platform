package microservice.auth.app.shared.exceptions;

import microservice.users.utils.exceptions.DrugstoreException;
import org.springframework.http.HttpStatus;

public class ValidationException extends DrugstoreException {
    public ValidationException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
