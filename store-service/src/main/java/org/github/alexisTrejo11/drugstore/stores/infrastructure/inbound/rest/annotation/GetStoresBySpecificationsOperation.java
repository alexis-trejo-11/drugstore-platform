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
		summary = "Search stores by specifications",
		description = """
				Advanced search endpoint that allows filtering stores by multiple criteria including:
				- Name, phone, email (partial match)
				- Code (exact match)
				- Geographic location (country, state, neighborhood)
				- Status
				- Location filters (proximity search, geographic boundaries)
				- Schedule filters (24-hour stores, currently open, specific day/time)

				**Requires JWT authentication with ADMIN role.**

				Supports pagination and sorting.
				""",
		tags = {"Store Query Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Stores retrieved successfully",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ResponseWrapper.class),
						examples = @ExampleObject(
								name = "Paginated Success Response",
								value = """
										{
										  "isSuccess": true,
										  "message": "Stores by specifications found successfully",
										  "data": {
										    "content": [
										      {
										        "id": "c1a2b3d4",
										        "code": "STR-001",
										        "name": "Central Pharmacy",
										        "status": "ACTIVE",
										        "phone": "+1-555-0123",
										        "email": "central@pharmacy.com",
										        "isOpen": true
										      }
										    ],
										    "page": 0,
										    "size": 10,
										    "totalElements": 25,
										    "totalPages": 3,
										    "first": true,
										    "last": false,
										    "hasNext": true,
										    "hasPrevious": false
										  },
										  "timestamp": "2025-10-19T14:25:30"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Bad request - Invalid search parameters",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": false,
										  "message": "Bad request",
										  "timestamp": "2025-10-19T14:25:30",
										  "error": {
										    "errorCode": "INVALID_PARAMETERS",
										    "message": "Invalid pagination parameters"
										  }
										}
										"""
						)
				)
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
public @interface GetStoresBySpecificationsOperation {
}
