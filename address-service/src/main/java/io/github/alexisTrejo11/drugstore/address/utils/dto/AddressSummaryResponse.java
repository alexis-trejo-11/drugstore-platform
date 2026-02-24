package io.github.alexisTrejo11.drugstore.address.utils.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary address response for listings")
public record AddressSummaryResponse(
    
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
) {}