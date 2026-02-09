package microservice.store_service.app.infrastructure.inbound.rest.annotation;

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
		summary = "Deactivate store",
		description = """
				Changes the store status to INACTIVE, making it unavailable for operations.

				**Requires JWT authentication with ADMIN role.**

				Use this to temporarily disable a store without deleting it.
				The store can be reactivated later.
				""",
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store deactivated successfully",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": true,
										  "message": "Store deactivated successfully",
										  "timestamp": "2025-10-19T14:25:30"
										}
										"""
						)
				)
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
public @interface DeactivateStoreOperation {
}
