package io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest.annotation;

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

/**
 * OpenAPI documentation decorator for the Update Current User Profile operation.
 *
 * Applies comprehensive documentation including operation summary, description,
 * success and error response examples for profile update scenarios.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
		summary = "Update current user's profile",
		description = """
				Updates the profile information of the authenticated user. Allows updating personal data 
				(name, date of birth, gender), biography, and profile picture. User ID is extracted from JWT Bearer token. 
				Empty or null values are treated as 'no update' for that field.
				
				**Requires JWT authentication.**
				
				Supports partial updates - only provide fields you want to change.
				"""
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Profile updated successfully",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ResponseWrapper.class),
						examples = @ExampleObject(
								name = "Success",
								value = """
										{
										  "message": "User profile updated successfully",
										  "data": {
										    "firstName": "John",
										    "lastName": "Doe",
										    "dateOfBirth": "19900515",
										    "gender": "MALE",
										    "bio": "Updated biography text",
										    "profilePictureUrl": "https://cdn.example.com/profiles/new-picture.jpg"
										  },
										  "timestamp": "2026-02-19T10:30:00"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Bad request - Validation errors",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								name = "Validation Error",
								value = """
										{
										  "message": "Validation failed",
										  "error": {
										    "code": "VALIDATION_ERROR",
										    "details": {
										      "firstName": "First name must be between 2 and 50 characters",
										      "dateOfBirth": "Date of birth must be in the past"
										    }
										  },
										  "timestamp": "2026-02-19T10:30:00"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "401",
				description = "Unauthorized - Missing or invalid JWT Bearer token",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								name = "Unauthorized",
								value = """
										{
										  "message": "Unauthorized",
										  "error": {
										    "code": "UNAUTHORIZED",
										    "details": "Valid JWT Bearer token required in Authorization header"
										  },
										  "timestamp": "2026-02-19T10:30:00"
										}
										"""
						)
				)
		),
		@ApiResponse(
				responseCode = "404",
				description = "Not Found - User or profile not found",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "422",
				description = "Unprocessable Entity - Business logic error",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "429",
				description = "Too Many Requests - Rate limit exceeded (100 requests/minute)",
				content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
				responseCode = "500",
				description = "Internal server error",
				content = @Content(mediaType = "application/json")
		)
})
public @interface UpdateProfileOperation {
}

