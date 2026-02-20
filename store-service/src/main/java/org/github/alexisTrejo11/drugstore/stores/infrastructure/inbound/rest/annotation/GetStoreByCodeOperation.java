package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import libs_kernel.response.ResponseWrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(
		summary = "Get store by code",
		description = "Retrieves a single store by its unique business code. **Requires JWT authentication with ADMIN role.**",
		tags = {"Store Query Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store found successfully",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ResponseWrapper.class),
						examples = @ExampleObject(
								name = "Success Response",
								value = """
										{
										  "isSuccess": true,
										  "message": "Store found successfully",
										  "data": {
										    "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
										    "code": "STR-001",
										    "name": "Central Pharmacy",
										    "status": "ACTIVE",
										    "phone": "+1-555-0123",
										    "email": "central@pharmacy.com",
										    "isOpen": true
										  },
										  "timestamp": "2025-10-19T14:25:30"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "404",
				description = "Store not found with the provided code",
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
public @interface GetStoreByCodeOperation {
}
