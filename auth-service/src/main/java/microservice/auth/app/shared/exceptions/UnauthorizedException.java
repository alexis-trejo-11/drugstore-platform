package microservice.auth.app.shared.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends DrugstoreException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "PH-401");
    }
}
