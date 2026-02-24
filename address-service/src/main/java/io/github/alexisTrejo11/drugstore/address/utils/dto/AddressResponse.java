package io.github.alexisTrejo11.drugstore.address.utils.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Address response object")
public record AddressResponse(
    
    @Schema(description = "Address unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,
    
    @Schema(description = "User ID who owns this address", example = "123e4567-e89b-12d3-a456-426614174001")
    String userId,
    
    @Schema(description = "Street address", example = "123 Main St")
    String street,
    
    @Schema(description = "City", example = "New York")
    String city,
    
    @Schema(description = "State/Province", example = "NY")
    String state,
    
    @Schema(description = "Country code", example = "US")
    String country,
    
    @Schema(description = "Postal/ZIP code", example = "10001")
    String postalCode,
    
    @Schema(description = "Additional address details", example = "Apt 4B")
    String additionalDetails,
    
    @Schema(description = "Whether this is the default address", example = "false")
    Boolean isDefault,
    
    @Schema(description = "Address creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Address last update timestamp")
    LocalDateTime updatedAt
) {}