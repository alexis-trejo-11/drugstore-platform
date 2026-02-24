package io.github.alexisTrejo11.drugstore.address.utils.exceptions;

import libs_kernel.exceptions.NotFoundException;

public class AddressNotFoundException extends NotFoundException {
	public AddressNotFoundException(String resourceName, String identifier, String identifierValue) {
		super(resourceName, identifier, identifierValue);
	}

	public AddressNotFoundException(String addressId) {
		super("Address", "id", addressId);
	}

	public AddressNotFoundException(String addressId, String userId) {
		super("Address", "id", addressId + " for user " + userId);
	}

}