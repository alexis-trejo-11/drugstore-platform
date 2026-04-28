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
		summary = "Update store schedule",
		description = """
				Updates the operating hours and schedule of a store.

				**Requires JWT authentication with ADMIN role.**

				Can configure different hours for each day of the week or set the store as 24/7.
				""",
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store schedule updated successfully",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": true,
										  "message": "Store schedule updated successfully",
										  "timestamp": "2025-10-19T14:25:30"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Bad request - Invalid schedule data",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "404",
				description = "Store not found",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "401",
				description = "Unauthorized",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "403",
				description = "Forbidden - ADMIN role required",
				content = @Content(mediaType = "application/json")
		)
})
public @interface UpdateStoreScheduleOperation {
}
