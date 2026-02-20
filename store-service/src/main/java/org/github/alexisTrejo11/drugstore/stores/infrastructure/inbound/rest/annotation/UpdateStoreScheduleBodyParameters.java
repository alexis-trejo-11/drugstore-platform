package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.ScheduleInsertRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@RequestBody(
		description = "New schedule configuration with operating hours per day",
		required = true,
		content = @Content(
				schema = @Schema(implementation = ScheduleInsertRequest.class),
				examples = @ExampleObject(
						value = """
								{
								  "monday": { "openTime": "07:00", "closeTime": "22:00" },
								  "tuesday": { "openTime": "07:00", "closeTime": "22:00" },
								  "wednesday": { "openTime": "07:00", "closeTime": "22:00" },
								  "thursday": { "openTime": "07:00", "closeTime": "22:00" },
								  "friday": { "openTime": "07:00", "closeTime": "23:00" },
								  "saturday": { "openTime": "08:00", "closeTime": "23:00" },
								  "sunday": { "openTime": "09:00", "closeTime": "20:00" }
								}
								"""
				)
		)
)
public @interface UpdateStoreScheduleBodyParameters {
}
