package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.CreateStoreRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RequestBody(
		description = "Store creation request with all required details",
		required = true,
		content = @Content(
				schema = @Schema(implementation = CreateStoreRequest.class),
				examples = @ExampleObject(
						name = "Complete Store Creation Request",
						value = """
                    {
                      "code": "STR-001",
                      "name": "Central Pharmacy",
                      "phone": "+1-555-0123",
                      "email": "central@pharmacy.com",
                      "address": {
                        "street": "123 Main St",
                        "city": "New York",
                        "state": "NY",
                        "zipCode": "10001",
                        "country": "USA"
                      },
                      "latitude": 40.7128,
                      "longitude": -74.0060,
                      "schedule": {
                        "monday": { "openTime": "08:00", "closeTime": "20:00" },
                        "tuesday": { "openTime": "08:00", "closeTime": "20:00" },
                        "wednesday": { "openTime": "08:00", "closeTime": "20:00" },
                        "thursday": { "openTime": "08:00", "closeTime": "20:00" },
                        "friday": { "openTime": "08:00", "closeTime": "20:00" },
                        "saturday": { "openTime": "09:00", "closeTime": "18:00" },
                        "sunday": { "openTime": "10:00", "closeTime": "16:00" }
                      }
                    }
                    """
				)
		)
)
public @interface CreateStoreBodyParameter {
}
