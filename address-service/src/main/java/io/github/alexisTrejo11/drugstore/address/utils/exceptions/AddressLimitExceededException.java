package io.github.alexisTrejo11.drugstore.address.utils.exceptions;

import org.springframework.http.HttpStatus;

public class AddressLimitExceededException extends AddressException {
	public AddressLimitExceededException(String userId, int limit, String userType) {
		super(
				String.format("User %s of type %s has reached the address limit of %d", userId, userType, limit),
				HttpStatus.FORBIDDEN,
				"ADDRESS_LIMIT_EXCEEDED");
	}
}
