package io.github.alexisTrejo11.drugstore.address.utils.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedAddressAccessException extends AddressException {
	public UnauthorizedAddressAccessException(String addressId, String userId) {
		super(
				String.format("User %s is not authorized to access address %s", userId, addressId),
				HttpStatus.FORBIDDEN,
				"ADDRESS_ACCESS_UNAUTHORIZED");
	}
}