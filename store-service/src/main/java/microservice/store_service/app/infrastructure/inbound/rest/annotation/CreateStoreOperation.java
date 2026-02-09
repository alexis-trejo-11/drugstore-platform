package microservice.store_service.app.infrastructure.inbound.rest.annotation;


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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
		summary = "Create a new store",
		description = """
            Creates a new store in the system with complete information including:
            - Basic details (name, code, contact info)
            - Address and geolocation
            - Operating schedule
            
            **Requires JWT authentication with ADMIN role.**
            
            The store will be created with ACTIVE status by default unless otherwise specified.
            """,
		tags = {"Store Command Operations"}
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "201",
				description = "Store created successfully",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ResponseWrapper.class),
						examples = @ExampleObject(
								name = "Store Creation Success",
								value = """
                    {
                      "isSuccess": true,
                      "message": "Entity created successfully",
                      "data": {
                        "storeID": "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Bad request - Validation errors or invalid data",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
                    {
                      "isSuccess": false,
                      "message": "Bad request",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "VALIDATION_FAILED",
                        "message": "Validation errors occurred",
                        "validationErrors": {
                          "name": "Store name is required",
                          "email": "Invalid email format",
                          "phone": "Phone number must be valid"
                        }
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
		),
		@ApiResponse(
				responseCode = "409",
				description = "Conflict - Store with the same code already exists",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								value = """
                    {
                      "isSuccess": false,
                      "message": "Conflict occurred",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "DUPLICATE_STORE_CODE",
                        "message": "A store with code STR-001 already exists"
                      }
                    }
                    """
						)
				)
		)
})
public @interface CreateStoreOperation {
}
