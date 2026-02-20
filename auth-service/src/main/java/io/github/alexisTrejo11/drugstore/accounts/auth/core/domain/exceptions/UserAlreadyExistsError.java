package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;


import libs_kernel.exceptions.ConflictException;

public class UserAlreadyExistsError extends ConflictException {
    public UserAlreadyExistsError(String message) {
        super(message, "USER_CREDENTIALS_CONFLICT");
    }
}
