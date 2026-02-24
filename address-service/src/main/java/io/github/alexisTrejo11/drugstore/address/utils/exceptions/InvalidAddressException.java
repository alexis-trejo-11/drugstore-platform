package io.github.alexisTrejo11.drugstore.address.utils.exceptions;

public class InvalidAddressException extends AddressException {
	public InvalidAddressException(String message) {
		super(message, "invalid_address");
	}
}