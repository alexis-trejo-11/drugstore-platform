package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.StoreLocationRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "New location details including address and coordinates",
		required = true,
		content = @Content(
				schema = @Schema(implementation = StoreLocationRequest.class),
				examples = @ExampleObject(
						value = """
											{
											  "address": {
											    "street": "456 Broadway Ave",
											    "city": "Los Angeles",
											    "state": "CA",
											    "zipCode": "90001",
											    "country": "USA"
											  },
											  "latitude": 34.0522,
											  "longitude": -118.2437
											}
											"""
				)
		)
)
public @interface UpdateStoreLocationBodyParameters {
	
}
