package org.github.alexisTrejo11.drugstore.stores.domain.exception;


public class StoreValidationException extends StoreDomainException {
	public StoreValidationException(String code) {
		super("Store with exactCode '" + code + "' already exists.");
	}
}
