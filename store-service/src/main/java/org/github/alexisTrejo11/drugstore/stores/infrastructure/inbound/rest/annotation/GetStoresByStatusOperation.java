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
		summary = "Get stores by status",
		description = """
				Retrieves all stores with a specific status (ACTIVE, INACTIVE, UNDER_MAINTENANCE, TEMPORARY_CLOSURE).

				**Requires JWT authentication with ADMIN role.**

				Returns paginated results with configurable page size and sorting.
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
								name = "Active Stores Response",
								value = """
										{
										  "isSuccess": true,
										  "message": "Stores by status found successfully",
										  "data": {
										    "content": [
										      {
										        "id": "c1a2b3d4",
										        "code": "STR-001",
										        "name": "Central Pharmacy",
										        "status": "ACTIVE",
										        "isOpen": true
										      },
										      {
										        "id": "e5f6g7h8",
										        "code": "STR-002",
										        "name": "Downtown Drugstore",
										        "status": "ACTIVE",
										        "isOpen": false
										      }
										    ],
										    "page": 0,
										    "size": 10,
										    "totalElements": 42,
										    "totalPages": 5,
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
				description = "Bad request - Invalid status value",
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
public @interface GetStoresByStatusOperation {
}
