package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidExpirationDateException extends ProductBaseException {
    public InvalidExpirationDateException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_EXPIRATION_DATE");
    }
}
