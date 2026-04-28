package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(
		summary = "Update store location",
		description = """
					Updates the physical location and geolocation coordinates of an existing store.
					
					**Requires JWT authentication with ADMIN role.**
					
					This operation updates both the address and GPS coordinates (latitude/longitude).
					""",
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store location updated successfully",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
											{
											  "isSuccess": true,
											  "message": "Operation completed successfully",
											  "timestamp": "2025-10-19T14:25:30"
											}
											"""
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Bad request - Invalid location data",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "404",
				description = "Store not found",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "401",
				description = "Unauthorized - Invalid or missing JWT token",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "403",
				description = "Forbidden - ADMIN role required",
				content = @Content(mediaType = "application/json")
		)
})
public @interface UpdateStoreLocationOperation {
}

