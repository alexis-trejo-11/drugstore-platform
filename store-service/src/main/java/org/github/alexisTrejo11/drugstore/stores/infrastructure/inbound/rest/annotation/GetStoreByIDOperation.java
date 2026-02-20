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
		summary = "Get store by ID",
		description = "Retrieves a single store by its unique identifier. **Requires JWT authentication with ADMIN role.**",
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
										  "message": "Store found successfully",
										  "data": {
										    "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
										    "code": "STR-001",
										    "name": "Central Pharmacy",
										    "status": "ACTIVE",
										    "phone": "+1-555-0123",
										    "email": "central@pharmacy.com",
										    "address": {
										      "street": "123 Main St",
										      "city": "New York",
										      "state": "NY",
										      "zipCode": "10001",
										      "country": "USA"
										    },
										    "latitude": 40.7128,
										    "longitude": -74.0060,
										    "isOpen": true,
										    "createdAt": "2025-01-15T10:30:00",
										    "updatedAt": "2025-10-19T14:20:00"
										  },
										  "timestamp": "2025-10-19T14:25:30"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "404",
				description = "Store not found",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": false,
										  "message": "Store not found",
										  "timestamp": "2025-10-19T14:25:30",
										  "error": {
										    "errorCode": "STORE_NOT_FOUND",
										    "message": "No store found with ID: c1a2b3d4-e5f6-7890-abcd-ef1234567890"
										  }
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "401",
				description = "Unauthorized - Invalid or missing JWT token",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": false,
										  "message": "Unauthorized access",
										  "timestamp": "2025-10-19T14:25:30",
										  "error": {
										    "errorCode": "UNAUTHORIZED",
										    "message": "Valid JWT token required"
										  }
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "403",
				description = "Forbidden - Insufficient permissions (ADMIN role required)",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": false,
										  "message": "Forbidden access",
										  "timestamp": "2025-10-19T14:25:30",
										  "error": {
										    "errorCode": "FORBIDDEN",
										    "message": "ADMIN role required to access this resource"
										  }
										}
										"""
						)
				)
		)
})
public @interface GetStoreByIDOperation {
}
