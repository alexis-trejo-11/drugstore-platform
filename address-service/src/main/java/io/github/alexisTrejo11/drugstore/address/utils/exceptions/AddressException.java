package io.github.alexisTrejo11.drugstore.address.utils.exceptions;

import libs_kernel.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class AddressException extends DomainException {
	public AddressException(String message, HttpStatus httpStatus, String errorCode) {
		super(message, httpStatus, errorCode);
	}

	public AddressException(String message, String errorCode) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
	}

	public AddressException(String message) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, "ADDRESS_ERROR");
	}
}