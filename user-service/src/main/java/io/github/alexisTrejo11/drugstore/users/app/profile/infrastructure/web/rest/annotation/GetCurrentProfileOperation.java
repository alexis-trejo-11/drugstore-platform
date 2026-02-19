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
 * OpenAPI documentation decorator for the Get Current User Profile operation.
 *
 * Applies comprehensive documentation including operation summary, description,
 * success and error response examples.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
		summary = "Get current user's profile",
		description = """
				Retrieves the profile information of the authenticated user. Returns personal data including name, 
				date of birth, gender, bio, and profile picture URL. User ID is extracted from JWT Bearer token.
				
				**Requires JWT authentication.**
				
				The profile contains all personal information associated with the user account.
				"""
)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "Profile retrieved successfully",
				content = @Content(
						mediaType = "application/json",
						schema = @Schema(implementation = ResponseWrapper.class),
						examples = @ExampleObject(
								name = "Success",
								value = """
										{
										  "message": "User profile retrieved successfully",
										  "data": {
										    "firstName": "John",
										    "lastName": "Doe",
										    "dateOfBirth": "19900515",
										    "gender": "MALE",
										    "bio": "Software developer passionate about clean code",
										    "profilePictureUrl": "https://cdn.example.com/profiles/550e8400-e29b-41d4-a716-446655440000.jpg"
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
				description = "Not Found - Profile not found for user",
				content = @Content(
						mediaType = "application/json",
						examples = @ExampleObject(
								name = "Profile Not Found",
								value = """
										{
										  "message": "Profile not found for user",
										  "error": {
										    "code": "PROFILE_NOT_FOUND",
										    "details": "No profile initialized for user 550e8400-e29b-41d4-a716-446655440000"
										  },
										  "timestamp": "2026-02-19T10:30:00"
										}
										"""
						)
				)
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
public @interface GetCurrentProfileOperation {
}

