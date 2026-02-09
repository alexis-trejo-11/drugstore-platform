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
		summary = "Set store under maintenance",
		description = """
				Changes the store status to UNDER_MAINTENANCE.

				**Requires JWT authentication with ADMIN role.**

				Use this when the store is temporarily closed for maintenance work.
				The store will not be available for operations until reactivated.
				""",
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store set to under maintenance successfully",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": true,
										  "message": "Store status updated to UNDER_MAINTENANCE",
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
public @interface SetUnderMaintenanceOperation {
}
