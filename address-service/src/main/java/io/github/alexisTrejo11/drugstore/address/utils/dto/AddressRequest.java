package io.github.alexisTrejo11.drugstore.address.utils.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for creating/updating an address")
public record AddressRequest(
    
    @Schema(description = "Street address", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street must not exceed 200 characters")
    String street,
    
    @Schema(description = "City", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,
    
    @Schema(description = "State/Province", example = "NY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    String state,
    
    @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "US", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Country is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a valid ISO 3166-1 alpha-2 code")
    String country,
    
    @Schema(description = "Postal/ZIP code", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    String postalCode,
    
    @Schema(description = "Additional address details", example = "Apt 4B")
    @Size(max = 200, message = "Additional details must not exceed 200 characters")
    String additionalDetails,
    
    @Schema(description = "Whether this is the default address", defaultValue = "false")
    Boolean isDefault
) {}