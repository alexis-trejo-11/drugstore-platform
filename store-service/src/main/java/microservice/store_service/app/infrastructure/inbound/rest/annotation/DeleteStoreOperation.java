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
		summary = "Delete store",
		description = """
				Permanently deletes a store from the system.

				**Requires JWT authentication with ADMIN role.**

				⚠️ **Warning:** This is a destructive operation and cannot be undone.
				Consider deactivating the store instead if you might need to restore it later.

				The store must not have any active purchaseOrders or pending operations before deletion.
				""",
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Store deleted successfully",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": true,
										  "message": "Store deleted successfully",
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
				responseCode = "400",
				description = "Bad request - Store cannot be deleted (has active dependencies)",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
										{
										  "isSuccess": false,
										  "message": "Bad request",
										  "timestamp": "2025-10-19T14:25:30",
										  "error": {
										    "errorCode": "CANNOT_DELETE_STORE",
										    "message": "Store has active purchaseOrders and cannot be deleted"
										  }
										}
										"""
						)
				)
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
public @interface DeleteStoreOperation {
}
