package libs_kernel.exceptions;

import org.springframework.http.HttpStatus;


public class NotFoundException extends DomainException {
    public NotFoundException(String resourceName, String identifier, String identifierValue) {
        super(resourceName + " with " + identifier + " " + identifierValue + " not found.",
                HttpStatus.NOT_FOUND, "ENTITY-404");
    }
}