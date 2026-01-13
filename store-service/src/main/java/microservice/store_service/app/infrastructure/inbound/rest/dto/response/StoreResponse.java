package microservice.store_service.app.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Store Response DTO")
public record StoreResponse(
        @Schema(description = "Store identifier", example = "c1a2b3d4")
    String id,

    @Schema(description = "Unique store exactCode", example = "STR-001")
    String code,

    @Schema(description = "Store name", example = "Central Pharmacy")
    String name,

    @Schema(description = "Store status", example = "ACTIVE")
    String status,

    @Schema(description = "Contact phone", example = "+123456789")
    String phone,

    @Schema(description = "Contact email", example = "store@example.com")
    String email,

    @Schema(description = "Store address")
    AddressResponse address,

    @Schema(description = "Geolocation latitude", example = "-12.046374")
    Double latitude,

    @Schema(description = "Geolocation longitude", example = "-77.042793")
    Double longitude,

    @Schema(description = "Whether the store is currently open", example = "true")
    boolean isOpen,

    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {}
