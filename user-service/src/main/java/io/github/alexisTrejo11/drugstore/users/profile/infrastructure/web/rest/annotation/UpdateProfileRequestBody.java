package io.github.alexisTrejo11.drugstore.users.profile.infrastructure.web.rest.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.github.alexisTrejo11.drugstore.users.profile.infrastructure.web.rest.dto.UpdateProfileRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenAPI documentation decorator for Update Profile request body parameter.
 *
 * Documents the request body structure and provides examples for profile update operations.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RequestBody(
		description = "Profile fields to update. Fields left null or empty will not be updated (partial updates supported).",
		required = true,
		content = @Content(
				schema = @Schema(implementation = UpdateProfileRequest.class),
				examples = @ExampleObject(
						name = "Update Profile Request",
						value = """
								{
								  "firstName": "John",
								  "lastName": "Doe",
								  "dateOfBirth": "1990-05-15T00:00:00",
								  "gender": "MALE",
								  "bio": "Software developer with 10 years of experience",
								  "profilePictureUrl": "https://cdn.example.com/profiles/john-doe.jpg"
								}
								"""
				)
		)
)
public @interface UpdateProfileRequestBody {
}

