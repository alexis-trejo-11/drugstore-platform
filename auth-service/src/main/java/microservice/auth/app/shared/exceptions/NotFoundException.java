package microservice.auth.app.shared.exceptions;

import user_service.utils.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends DomainException {
    public NotFoundException(String resourceName, String identifier) {
        super(resourceName + " with ID " + identifier + " not found.",
                HttpStatus.NOT_FOUND, "PH-404");
    }
}