package io.github.alexisTrejo11.drugstore.address.utils.dto;

import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary address response for listings")
public record AddressSummary(
    
    @Schema(description = "Address unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,
    
    @Schema(description = "Street address (first 50 chars)", example = "123 Main St")
    String street,
    
    @Schema(description = "City", example = "New York")
    String city,
    
    @Schema(description = "Country code", example = "US")
    String country,
    
    @Schema(description = "Whether this is the default address", example = "false")
    Boolean isDefault
) {

	public static AddressSummary fromEntity(AddressEntity entity) {
		String shortStreet = entity.getStreet().length() > 50 ?
				entity.getStreet().substring(0, 47) + "..." :
				entity.getStreet();

		return new AddressSummary(
				entity.getId() != null ? entity.getId().toString() : null,
				shortStreet,
				entity.getCity(),
				entity.getCountry(),
				entity.getIsDefault()
		);
	}
}